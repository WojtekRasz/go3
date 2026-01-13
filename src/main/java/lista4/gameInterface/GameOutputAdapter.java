package lista4.gameInterface;

import lista4.gameLogic.Board;
import lista4.gameLogic.state.GameState;
import lista4.gameLogic.PlayerColor;
import java.io.PrintWriter;

/**
 * Defines the contract for sending game updates to clients.
 *
 * This interface abstracts the output layer, allowing the game logic to send
 * information without knowing if the client is using a Console or a GUI.
 * Implementations of this interface handle the specific formatting and
 * transmission protocols.
 *
 * @param <OutputType> The type of raw output.
 */
public interface GameOutputAdapter<OutputType> {

    /**
     * Registers a player and associates them with an output stream.
     *
     * This method binds a specific PlayerColor to a network socket output,
     * allowing the server to direct messages to the correct client.
     *
     * @param color The color of the player to register.
     * @param out   The PrintWriter stream connected to the client's socket.
     */
    void registerPlayer(PlayerColor color, PrintWriter out);

    /**
     * Removes a player from the active connections list.
     *
     * This is typically called when a client disconnects or the game ends,
     * ensuring no further messages are attempted to be sent to a closed socket.
     *
     * @param color The color of the player to remove.
     */
    void unregisterPlayer(PlayerColor color);

    /**
     * Sends the current state of the game to the specified target.
     *
     * Used to inform players about whose turn it is, if the game has ended,
     * or if they are waiting for an opponent.
     *
     * @param gameState The current status of the game logic.
     * @param target    The recipient (Specific player or BOTH).
     */
    void sendState(GameState gameState, PlayerColor target);

    /**
     * Sends the current board layout to the specified target.
     *
     * The implementation determines how the board is represented:
     * - Console adapter: Sends an ASCII art string.
     * - GUI adapter: Sends protocol commands (e.g., UPDATE WHITE 10 10).
     *
     * @param board  The current game board object.
     * @param target The recipient (Specific player or BOTH).
     */
    void sendBoard(Board board, PlayerColor target);

    /**
     * Sends an error or exception message to a specific player.
     *
     * This is used to notify a player about invalid moves or rule violations
     * without crashing the server.
     *
     * @param exception The exception containing the error details.
     * @param target    The player who caused the error.
     */
    void sendExceptionMessage(Exception exception, PlayerColor target);

    /**
     * Broadcasts a generic message to all connected players.
     *
     * @param message The message content to send.
     */
    void sendBroadcast(String message);

    void sendWiningMassage(PlayerColor playerColor, int whiteStones, int blackStones, boolean byGivingUp);

    void sendCurrentPlayer(PlayerColor playerColor);

    void sendNegotiationStart();

    void sendEndOfNegotiationToPlayer(PlayerColor playerColor);
}