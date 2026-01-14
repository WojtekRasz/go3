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
 * transmission protocols (e.g., ASCII art for console, raw commands for GUI).
 *
 * @param <OutputType> The type of raw output handled by the adapter.
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
     * Sends the current high-level state of the game to the specified target.
     *
     * Used to inform players about global game status, such as waiting for
     * opponents,
     * game over states, or active gameplay.
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
     * - GUI adapter: Sends protocol commands (e.g., "UPDATE BLACK 10 10").
     *
     * @param board  The current game board object.
     * @param target The recipient (Specific player or BOTH).
     */
    void sendBoard(Board board, PlayerColor target);

    /**
     * Sends an error or exception message to a specific player.
     *
     * This is used to notify a player about invalid moves or rule violations
     * without crashing the server or disrupting the other player.
     *
     * @param exception The exception containing the error details.
     * @param target    The player who caused the error.
     */
    void sendExceptionMessage(Exception exception, PlayerColor target);

    /**
     * Broadcasts a generic text message to all connected players.
     *
     * @param message The message content to send.
     */
    void sendBroadcast(String message);

    /**
     * Sends the final game result message.
     *
     * This method informs players of the winner and the final score (territories +
     * captures).
     *
     * @param playerColor The winner of the game.
     * @param whiteStones The total score of the White player.
     * @param blackStones The total score of the Black player.
     * @param byGivingUp  True if the game ended due to resignation, false if by
     *                    score.
     */
    void sendWiningMassage(PlayerColor playerColor, int whiteStones, int blackStones, boolean byGivingUp);

    /**
     * Sends a raw message string to a specific player.
     *
     * @param message The raw string to send.
     * @param target  The recipient of the message.
     */
    void sendToTarget(String message, PlayerColor target);

    /**
     * Notifies players about whose turn it is currently.
     *
     * @param playerColor The color of the player who should make the next move.
     */
    void sendCurrentPlayer(PlayerColor playerColor);

    /**
     * Broadcasts a signal that the negotiation phase has started.
     *
     * This instructs the clients to switch their UI to territory selection mode.
     */
    void sendNegotiationStart();

    /**
     * Notifies a player that their opponent has proposed a territory arrangement.
     *
     * This signal prompts the player to either accept the proposal or reject it
     * (resume game).
     *
     * @param playerColor The player who needs to respond to the proposal.
     */
    void sendEndOfNegotiationToPlayer(PlayerColor playerColor);

    /**
     * Updates the clients with the current count of captured stones.
     *
     * @param blackCaptured Total stones captured by the Black player.
     * @param WhiteCaptured Total stones captured by the White player.
     */
    void sendCaptureStonesQuantity(int blackCaptured, int WhiteCaptured);

    /**
     * Sends a specific update regarding a territory proposal on a single field.
     *
     * Used during the negotiation phase to synchronize markers on the board.
     *
     * @param x           The X coordinate of the field.
     * @param y           The Y coordinate of the field.
     * @param playerColor The color associated with the territory proposal.
     * @param update_type The type of update (e.g., "+" to add, "-" to remove).
     */
    void sendTeritoryUpdate(int x, int y, PlayerColor playerColor, String update_type);

    /**
     * Signals that the game has been resumed from the negotiation phase.
     *
     * This clears the negotiation UI and redraws the board state.
     *
     * @param board The current state of the board to be redrawn.
     */
    void resumeGame(Board board);
}