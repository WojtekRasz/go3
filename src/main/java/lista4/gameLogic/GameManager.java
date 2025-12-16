package lista4.gameLogic;

import lista4.gameInterface.GameOutputAdapter;
import lista4.gameLogic.gameExceptions.GameNotRunningException;
import lista4.gameLogic.gameExceptions.OtherPlayersTurnException;
import lista4.gameLogic.gameExceptions.OutputException;
import lista4.gameLogic.state.GameState;
import lista4.gameLogic.PlayerColor;

public class GameManager {

    // tutaj już tworzę instancje, konstruktor jest private (wziąłem z przykładu)
    private static GameManager instance = new GameManager();
    private final GameContext gameContext;
    private final Board board;
    private GameOutputAdapter outAdapter; // dodałem out Adapter do gry on później wyśle result do klientów

    // ----------------------------------------Sekcja
    // techniczna------------------------------------------------------

    private GameManager() {
        gameContext = new GameContext(GameState.GAME_NOT_RUNNING);
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

    // ---------------------------------------Sekcja start/stop
    // gry--------------------------------------------------

    public void startGame() {
        gameContext.startGame();
        outAdapter.sendState(gameContext.getGameState(), PlayerColor.BOTH);
    }

    public void endGame() {
        gameContext.endGame();
        outAdapter.sendState(gameContext.getGameState(), PlayerColor.BOTH);
    }

    // ---------------------------------------Sekcja
    // ruchów---------------------------------------
    public void makeMove(Move move) {
        try {
            if (gameContext.getGameState() == GameState.GAME_NOT_RUNNING) {
                throw new GameNotRunningException("gra się nie rozpoczęła.");
            }
            if (move.playerColor == PlayerColor.BLACK && gameContext.getGameState() == GameState.WHITE_MOVE) {
                throw new OtherPlayersTurnException(PlayerColor.WHITE);
            }
            if (move.playerColor == PlayerColor.WHITE && gameContext.getGameState() == GameState.BLACK_MOVE) {
                throw new OtherPlayersTurnException(PlayerColor.BLACK);
            }

            Stone stone = new Stone(move.x, move.y, move.playerColor, board);
            board.putStone(move.x, move.y, stone);

            outAdapter.sendBoard(board, PlayerColor.BOTH);

            gameContext.nextPlayer();

            // outAdapter.sendState(gameContext.getGameState(), PlayerColor.BOTH);
        } catch (OutputException e) {
            outAdapter.sendExceptionMessage(e, move.playerColor);
        }

    }

    // poniżej dodałem
    public String simulateMove(String message) { // może być voidem ale to do sprawdzania na razie
        try {
            Thread.sleep(100); // tutaj gra coś sobie sprawdza
        } catch (InterruptedException e) {

        }
        outAdapter.sendBoard(board, PlayerColor.BOTH);
        return "moved after: 1s";
    }

    // tu koniec dodawania

}
