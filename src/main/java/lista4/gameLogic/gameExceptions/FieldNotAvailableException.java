package lista4.gameLogic.gameExceptions;

import lista4.gameLogic.GameManager;

public class FieldNotAvailableException extends RuntimeException {
    private final GameManager.Move move;
    public FieldNotAvailableException(GameManager.Move move) {
        this.move = move;
    }

    public GameManager.Move getMove(){
        return move;
    }
}
