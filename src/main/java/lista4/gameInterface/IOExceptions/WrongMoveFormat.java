package lista4.gameInterface.IOExceptions;

/**
 * Indicates that the input string provided for a game move does not adhere
 * to the required syntax or format.
 *
 * This exception is typically thrown by the input adapters (like
 * GameInputAdapter)
 * when parsing user commands. For example, if a user types "Z 99" but the
 * board only supports "A-S", this exception ensures the game logic is not
 * executed with invalid data.
 */
public class WrongMoveFormat extends RuntimeException {

    /**
     * Constructs a new WrongMoveFormat exception with the specified detail message.
     *
     * @param message The detail message explaining why the format was rejected.
     */
    public WrongMoveFormat(String message) {
        super(message);
    }
}