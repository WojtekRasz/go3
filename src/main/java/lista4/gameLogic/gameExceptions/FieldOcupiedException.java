package lista4.gameLogic.gameExceptions;

import lista4.gameLogic.Move;

public class FieldOcupiedException extends FieldNotAvailableException {
    public FieldOcupiedException(Move move) {
        super("To pole jest poza planszą.");
        this.move = move;
    }

    private final Move move;

    public Move getMove() {
        return move;
    }
}
