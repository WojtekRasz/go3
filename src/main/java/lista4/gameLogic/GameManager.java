package lista4.gameLogic;

import lista4.gameInterface.GameOutputAdapter;
import lista4.gameLogic.gameExceptions.GameNotRunningException;
import lista4.gameLogic.gameExceptions.NegotiationsNotPresent;
import lista4.gameLogic.gameExceptions.OtherPlayersTurnException;
import lista4.gameLogic.state.GameState;

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

    /**
     * Private constructor for singleton pattern.
     * Initializes the board and sets the initial player.
     */
    private GameManager() {
        gameContext = new GameContext(GameState.GAME_NOT_INITIALIZED);
        gameContext.setCurPlayerColor(PlayerColor.BLACK);
        board = new Board();
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

    /**
     * Makes move and send board and current player to output (eventually error instead if occurs)
     *
     * @param move Move that is meant to be done
     */
    public void makeMove(Move move) {
        try {
            Exception canMakeMove = canMakeMove(move.playerColor);
            if (canMakeMove != null)
                throw canMakeMove;

            Stone stone = new Stone(move.x, move.y, move.playerColor, board);
            board.putStone(move.x, move.y, stone);

            outAdapter.sendBoard(board, PlayerColor.BOTH);
            sendCaptured();
            gameContext.resetPasses();
            gameContext.nextPlayer();

            outAdapter.sendCurrentPlayer(gameContext.getCurPlayerColor());
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
     * @param playerColor - player who ended negotiations
     */
    public void proposeFinishNegotiation(PlayerColor playerColor) {
        outAdapter.sendEndOfNegotiationToPlayer(playerColor.other());
        colorOfProposal = playerColor;
    }

    // Gdy 2 się zgodzi negocjacje się kończą

    /**
     * Finishes negotiation after 2nd player accepted
     * @param color - player who finished negotiations (to validate its not the same who made proposal)
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
     * @param playerColor - player who gives up
     */
    public void giveUpGame(PlayerColor playerColor) {
        outAdapter.sendWiningMassage(playerColor.other(), 0, 0, true);

        gameContext.finishGame();
    }

    // Dodaj terytorium

    /**
     * Add territory in negotiations
     * @param playerColor - color of territory
     * @param x - x cord of territory
     * @param y - y cord of territory
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
     * @param playerColor - color of territory
     * @param x - x cord of territory
     * @param y - y cord of territory
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
                gameContext.getCaptured(PlayerColor.WHITE)
        );
    }
}
