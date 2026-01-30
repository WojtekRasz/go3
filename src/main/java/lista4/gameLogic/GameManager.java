package lista4.gameLogic;

import lista4.backend.BotService;
import lista4.dbModel.GameEntity;
import lista4.dbModel.MoveEntity;
import lista4.dbRepositories.GameRepository;
import lista4.dbRepositories.MoveRepository;
import lista4.gameInterface.GameOutputAdapter;
import lista4.gameLogic.gameExceptions.GameNotRunningException;
import lista4.gameLogic.gameExceptions.NegotiationsNotPresent;
import lista4.gameLogic.gameExceptions.OtherPlayersTurnException;
import lista4.gameLogic.state.GameState;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class responsible for managing the overall game flow.
 * 
 * It maintains the board, current game context, and communicates with the
 * output adapter.
 * Handles starting/stopping the game, player moves, and turn management.
 */
public class GameManager {

    /** Singleton instance of the GameManager */
    private static GameManager instance = new GameManager();

    /** Game context, storing the current state and current player */
    private final GameContext gameContext;

    /** The board on which the game is played */
    private final Board board;

    /** Adapter used to send updates to clients (GUI/console) */
    private GameOutputAdapter outAdapter;

    /** Player who send the proposition to end negotiations */
    private PlayerColor colorOfProposal;

    private GameRepository gameRepository;
    private MoveRepository moveRepository;

    @Autowired
    private BotService botService; // Twój algorytm mapy wpływów

    private boolean vsBot = false;
    private PlayerColor botColor;

    // Metoda wołana z ClientThread, gdy gracz wpisze "bot"
    public void activateBot(PlayerColor color) {
        this.vsBot = true;
        this.botColor = color;
        botService = new BotService();
        System.out.println("Bot aktywowany jako: " + color);
        if (gameContext.getCurPlayerColor() == botColor) {
            triggerBotMove();
        }
    }

    public void deActivateBot(PlayerColor color) {
        this.vsBot = false;
        this.botColor = null;
        botService = null;
        System.out.println("Bot dekatywowany " + color);
    }

    private void triggerBotMove() {
        // 1. Bot wylicza ruch (przekazujemy mu obecną macierz planszy)
        try {
            String bestMoveCoords = botService.calculateBestMove(board.getMatrix(), botColor);
            String[] coords = bestMoveCoords.split(" ");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);

            // 2. Jeśli bot zwrócił -1 -1 (brak ruchu), niech spasuje
            if (x == -1 || y == -1) {
                passMove(botColor);
                return;
            }

            // 3. Wykonaj logikę ruchu
            executeMoveLogic(new Move(x, y, botColor));
        } catch (Exception e) {
            // Jeśli bot się pomyli (np. samobójstwo), niech spasuje
            System.err.println("Bot popełnił błąd, pasuje: " + e.getMessage());
            passMove(botColor);
        }
    }

    /**
     * Private constructor for singleton pattern.
     * Initializes the board and sets the initial player.
     */
    private GameManager() {
        gameContext = new GameContext(GameState.GAME_NOT_INITIALIZED);
        gameContext.setCurPlayerColor(PlayerColor.BLACK);
        board = new Board();
    }

    public void setRepositories(GameRepository gameRepository, MoveRepository moveRepository) {
        this.gameRepository = gameRepository;
        this.moveRepository = moveRepository;
    }

    /**
     * Returns the singleton instance of GameManager.
     * 
     * @return GameManager instance
     */
    public static GameManager getInstance() {
        if (instance == null) {
            synchronized (GameManager.class) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }

    /**
     * Resets the GameManager instance for testing purposes.
     */
    public static void resetForTests() {
        instance = new GameManager();
    }

    /**
     * Sets the output adapter used to communicate game updates.
     * 
     * @param adapter The output adapter
     */
    public void setAdapter(GameOutputAdapter adapter) {
        this.outAdapter = adapter;
    }

    /**
     * Returns the current output adapter.
     * 
     * @return GameOutputAdapter instance
     */
    public GameOutputAdapter getAdapter() {
        return outAdapter;
    }

    /**
     * Returns the board object.
     * 
     * @return Current Board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sends the current board state to the specified player.
     * 
     * @param color PlayerColor who should receive the board update
     */
    public void sendBoard(PlayerColor color) {
        outAdapter.sendBoard(board, color);
    }

    // ---------------------- Game start/stop ----------------------------

    /**
     * Starts the game and notifies all players.
     */
    public void startGame() {
        if(gameContext.getGameState() == GameState.GAME_NOT_INITIALIZED){
            GameEntity gameEntity = new GameEntity();
            gameEntity.setStartTime(LocalDateTime.now());
            gameRepository.save(gameEntity);
            gameContext.setCurGameEntity(gameEntity);
        }
        gameContext.startGame();


        outAdapter.sendState(gameContext.getGameState(), PlayerColor.BOTH);
        outAdapter.sendCurrentPlayer(gameContext.getCurPlayerColor());


    }

    /**
     * Ends the game and notifies all players.
     */
    public void endGame() {
        gameContext.finishGame();
        outAdapter.sendState(gameContext.getGameState(), PlayerColor.BOTH);
    }

    /**
     * Pauses the game (wait state) and notifies all players.
     */
    public void waitGame() {
        gameContext.finishGame();
        outAdapter.sendState(gameContext.getGameState(), PlayerColor.BOTH);
    }

    // ---------------------- Moves / Turns ------------------------------

    /**
     * Checks if it's the given player's turn.
     * 
     * @param playerColor PlayerColor to check
     * @return true if it's the player's turn, false otherwise
     */
    private boolean isPlayersTurn(PlayerColor playerColor) {
        // true if players' turn, false otherwise
        return gameContext.getCurPlayerColor() == playerColor;
    }

    /**
     * Checks if the player is allowed to make a move.
     * 
     * @param playerColor Player attempting the move
     * @return Exception describing why the move cannot be made, or null if allowed
     */
    private Exception canMakeMove(PlayerColor playerColor) {
        if (gameContext.getGameState() != GameState.GAME_RUNNING) {
            return new GameNotRunningException("The game has not started.");
        }
        if (!isPlayersTurn(playerColor)) {
            return new OtherPlayersTurnException(playerColor.other());
        }
        return null;
    }

    private PlayerColor calculateWining() {
        if (gameContext.whitePoints() > gameContext.blackPoints()) {
            return PlayerColor.WHITE;
        } else if (gameContext.blackPoints() > gameContext.whitePoints()) {
            return PlayerColor.BLACK;
        }
        return PlayerColor.BOTH;
    }

    // Robi ruchy

    private void executeMoveLogic(Move move) throws Exception {
        // 1. Walidacja
        Exception canMakeMove = canMakeMove(move.playerColor);
        if (canMakeMove != null)
            throw canMakeMove;

        // 2. Logika na planszy
        Stone stone = new Stone(move.x, move.y, move.playerColor, board);
        board.putStone(move.x, move.y, stone);

        // 3. Zapis do DB (używamy wstrzykniętego moveRepository)
        MoveEntity moveEntity = new MoveEntity();
        moveEntity.setGame(gameContext.getCurGameEntity());
        moveEntity.setMoveNumber(gameContext.getMoveNumber());
        moveEntity.setX(move.x);
        moveEntity.setY(move.y);
        moveEntity.setColor(move.playerColor.toString());
        moveEntity.setPass(false);
        moveRepository.save(moveEntity);

        // 4. Komunikacja
        outAdapter.sendBoard(board, PlayerColor.BOTH);
        sendCaptured();
        gameContext.resetPasses();
        gameContext.nextPlayer();
        outAdapter.sendCurrentPlayer(gameContext.getCurPlayerColor());
    }

    /**
     * Makes move and send board and current player to output (eventually error
     * instead if occurs)
     *
     * @param move Move that is meant to be done
     */
    public void makeMove(Move move) {
        try {
            executeMoveLogic(move);

            if (vsBot && gameContext.getCurPlayerColor() == botColor
                    && gameContext.getGameState() == GameState.GAME_RUNNING) {
                new Thread(() -> {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                    }
                    triggerBotMove();
                }).start();
            }

        } catch (Exception e) {
            outAdapter.sendExceptionMessage(e, move.playerColor);
        }
    }

    /**
     * Player passes their turn.
     * Switches to next player and updates game state.
     * 
     * @param playerColor Player who passes
     */
    public void passMove(PlayerColor playerColor) {
        try {
            Exception canMakeMove = canMakeMove(playerColor);
            if (canMakeMove != null)
                throw canMakeMove;

            gameContext.passNextPlayer();

            MoveEntity move = new MoveEntity();
            move.setGame(gameContext.getCurGameEntity());
            move.setMoveNumber(gameContext.getMoveNumber());
            move.setX(-1);
            move.setY(-1);
            move.setColor(playerColor.toString());
            move.setPass(true);
            moveRepository.save(move);

            if (gameContext.getConsecutivePasses() == 2) {
                gameContext.startNegotiations();
                gameContext.resetPasses();
                outAdapter.sendState(gameContext.getGameState(), PlayerColor.BOTH);
            } else {
                outAdapter.sendCurrentPlayer(playerColor.other());
            }

        } catch (Exception e) {
            outAdapter.sendExceptionMessage(e, playerColor);
        }
    }

    // Wznawia gre po nieudanych negocjacjach. Ustawia ture gracza na przeciwnika
    // tego co przerwał

    /**
     * Resume game after fail in negotiation
     * 
     * @param playerColor - player who stopped negotiations
     */
    public void resumeGame(PlayerColor playerColor) {

        gameContext.setCurPlayerColor(playerColor.other());
        outAdapter.sendState(gameContext.getGameState(), PlayerColor.BOTH);
        outAdapter.sendBroadcast("RESUME_GAME");
        outAdapter.resumeGame(board);
        gameContext.clearTerritories();
        gameContext.resumeGame();
        outAdapter.sendCurrentPlayer(playerColor.other());
    }

    // Uruchamiane gdy jeden z graczy zakończył negocjacje i czeka na drugiego

    /**
     * Sending to other player negotiation result to accept or refuse it
     * 
     * @param playerColor - player who ended negotiations
     */
    public void proposeFinishNegotiation(PlayerColor playerColor) {
        outAdapter.sendEndOfNegotiationToPlayer(playerColor.other());
        colorOfProposal = playerColor;
    }

    // Gdy 2 się zgodzi negocjacje się kończą

    /**
     * Finishes negotiation after 2nd player accepted
     * 
     * @param color - player who finished negotiations (to validate its not the same
     *              who made proposal)
     */
    public void finishNegotiation(PlayerColor color) {
        if (colorOfProposal != color) {
            PlayerColor winner = calculateWining();
            outAdapter.sendWiningMassage(winner, gameContext.whitePoints(), gameContext.blackPoints(), false);
            gameContext.finishGame();
        } else {
            outAdapter.sendToTarget("To jest Twoja propozycja", color);
        }
    }

    // Poddaj gre

    /**
     * Give up game
     * 
     * @param playerColor - player who gives up
     */
    public void giveUpGame(PlayerColor playerColor) {
        outAdapter.sendWiningMassage(playerColor.other(), 0, 0, true);

        gameContext.finishGame();
    }

    // Dodaj terytorium

    /**
     * Add territory in negotiations
     * 
     * @param playerColor - color of territory
     * @param x           - x cord of territory
     * @param y           - y cord of territory
     */
    public void addTerritory(PlayerColor playerColor, int x, int y) {
        if (gameContext.getGameState() != GameState.NEGOTIATIONS) {
            outAdapter.sendExceptionMessage(new NegotiationsNotPresent(""), playerColor);
            return;
        }

        gameContext.addTerritory(playerColor, x, y);
        outAdapter.sendTeritoryUpdate(x, y, playerColor, "+");
    }

    /**
     * Removes territory in negotiations
     * 
     * @param playerColor - color of territory
     * @param x           - x cord of territory
     * @param y           - y cord of territory
     */
    public void removeTerritory(PlayerColor playerColor, int x, int y) {
        if (gameContext.getGameState() != GameState.NEGOTIATIONS) {
            outAdapter.sendExceptionMessage(new NegotiationsNotPresent(""), playerColor);
            return;
        }

        gameContext.removeTerritory(playerColor, x, y);
        outAdapter.sendTeritoryUpdate(x, y, playerColor, "-");
    }

    /**
     * Adding captured stone of color
     * 
     * @param playerColor - color of captured stone
     */
    public void addCaptured(PlayerColor playerColor) {
        gameContext.addCaptured(playerColor);
    }

    /**
     * Sending captured stone quantity of each color to output
     */
    public void sendCaptured() {
        outAdapter.sendCaptureStonesQuantity(
                gameContext.getCaptured(PlayerColor.BLACK),
                gameContext.getCaptured(PlayerColor.WHITE));
    }

    public void loadGame(GameEntity gameEntity) {
        board.clearBoard();
        gameContext.setCurGameEntity(gameEntity);
        gameContext.setGameState(GameState.GAME_RUNNING);
        List<MoveEntity> moves = moveRepository.findByGameOrderByMoveNumberAsc(gameEntity);
        for (MoveEntity move : moves) {
            if (move.isPass()) {
                gameContext.setCurPlayerColor(PlayerColor.valueOf(move.getColor()).other());
                continue;
            }
            Stone stone = new Stone(move.getX(), move.getY(), PlayerColor.valueOf(move.getColor()), board);
            board.putStone(move.getX(), move.getY(), stone);
            gameContext.setCurPlayerColor(PlayerColor.valueOf(move.getColor()).other());
        }
    }

    public void sendGameList(PlayerColor playerColor) {
        List<GameEntity> games = gameRepository.findAll();
        outAdapter.sendGamesList(games, playerColor);
    }

    public void loadGameById(Long gameId) {
        GameEntity gameEntity = gameRepository.findById(gameId).orElse(null);
        if (gameEntity != null) {
            loadGame(gameEntity);
        }
    }
}
