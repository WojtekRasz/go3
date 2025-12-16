package lista4.gameLogic.gameExceptions;

import lista4.gameLogic.Move;

public class FieldNotAvailableException extends RuntimeException {
    public FieldNotAvailableException(Move move) {
        super("To pole nie jest dostępne.");
        this.move = move;
    }

    private final Move move;

    public Move getMove() {
        return move;
    }
}
