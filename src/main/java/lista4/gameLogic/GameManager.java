package lista4.gameLogic;

import lista4.gameInterface.GameOutputAdapter;
import lista4.gameLogic.gameExceptions.GameNotRunningException;
import lista4.gameLogic.gameExceptions.OtherPlayersTurnException;
import lista4.gameLogic.state.GameState;

public class GameManager {

    // tutaj już tworzę instancje, konstruktor jest private (wziąłem z przykładu)
    private static GameManager instance = new GameManager();
    private final GameContext gameContext;
    private final Board board;
    private GameOutputAdapter outAdapter; // dodałem out Adapter do gry on później wyśle result do klientów

    // ----------------------------------------Sekcja
    // techniczna------------------------------------------------------

    private GameManager() {
        gameContext = new GameContext(GameState.GAME_NOT_INITIALIZED);
        gameContext.setCurPlayerColor(PlayerColor.BLACK);
        board = new Board();
    }

    public static GameManager getInstance() { // double checking jak w poprzedniej liście
        if (instance == null) {
            synchronized (GameManager.class) {
                if (instance == null) {
                    System.out.println("Instance is null for Thread " + Thread.currentThread().getId());
                    instance = new GameManager();
                    System.out.println(
                            "Returing " + instance.hashCode() + " instance to Thread "
                                    + Thread.currentThread().getId());
                }
            }
        }
        return instance;
    }

    public void setAdapter(GameOutputAdapter adapter) {
        this.outAdapter = adapter;
    }

    public GameOutputAdapter getAdapter() {
        return outAdapter;
    }

    public Board getBoard() {
        return board;
    }

    public void sendBoard(PlayerColor color) {
        outAdapter.sendBoard(board, color);
    }
    // ---------------------------------------Sekcja start/stop
    // gry--------------------------------------------------

    public void startGame() {
        gameContext.startGame();
        outAdapter.sendState(gameContext.getGameState(), PlayerColor.BOTH);
    }

    public void endGame() {
        gameContext.stopGame();
        outAdapter.sendState(gameContext.getGameState(), PlayerColor.BOTH);
    }

    public void waitGame() {
        gameContext.stopGame();
        outAdapter.sendState(gameContext.getGameState(), PlayerColor.BOTH);
    }

    // ---------------------------------------Sekcja
    // ruchów---------------------------------------

    private boolean isPlayersTurn(PlayerColor playerColor) {
        // true if players' turn, false otherwise
        return gameContext.getPlayerColor() == playerColor;
    }

    private Exception canMakeMove(PlayerColor playerColor) {
        if (gameContext.getGameState() != GameState.GAME_RUNNING) {
            return new GameNotRunningException("gra się nie rozpoczęła.");
        }
        if(!isPlayersTurn(playerColor)){
            return new OtherPlayersTurnException(playerColor.other());
        }
        return null;
    }

    public void makeMove(Move move) {
        try {
            Exception canMakeMove = canMakeMove(move.playerColor);
            if (canMakeMove != null) throw canMakeMove;

            Stone stone = new Stone(move.x, move.y, move.playerColor, board);
            board.putStone(move.x, move.y, stone);

            outAdapter.sendBoard(board, PlayerColor.BOTH);
            gameContext.resetPasses();

            gameContext.nextPlayer();

            outAdapter.sendState(gameContext.getGameState(), PlayerColor.BOTH);
        } catch (Exception e) {
            outAdapter.sendExceptionMessage(e, move.playerColor);
        }

    }

    public void passMove(PlayerColor playerColor) {
        try {
            Exception canMakeMove = canMakeMove(playerColor);
            if (canMakeMove != null) throw canMakeMove;

            gameContext.passNextPlayer();
            gameContext.stopGame();


        } catch (Exception e) {
            outAdapter.sendExceptionMessage(e, playerColor);
        }
    }

    public void resumeGame(PlayerColor playerColor) {
        gameContext.setCurPlayerColor(playerColor);

    }

}
