package lista4.gameLogic.gameExceptions;

import lista4.gameLogic.Move;

public class SuicideException extends FieldNotAvailableException {
    private Move move;
    public SuicideException(Move move) {
        super("Ruch jest samobójczy.");
        this.move = move;
    }

    public Move getMove() {
        return  move;
    }
}
