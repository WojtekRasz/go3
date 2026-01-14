package lista4.gameLogic.gameExceptions;

public class CaptureInKoException extends FieldNotAvailableException {
    public CaptureInKoException() {
        super("Ruch doprowadzi do bicia w KO");
    }
}
