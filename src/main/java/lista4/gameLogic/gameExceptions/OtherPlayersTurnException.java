package lista4.gameLogic.gameExceptions;

public class OtherPlayersTurnException extends RuntimeException {
    public OtherPlayersTurnException(String message) {
        super(message);
    }
}
