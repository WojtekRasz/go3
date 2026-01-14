package lista4.gameLogic.gameExceptions;

public abstract class FieldNotAvailableException extends RuntimeException {
    public FieldNotAvailableException(String message) {
        super(message);
    }
}
