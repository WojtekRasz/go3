package lista4.adapters;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lista4.dbModel.GameEntity;
import lista4.gameInterface.GameOutputAdapter;
import lista4.gameLogic.Board;
import lista4.gameLogic.state.GameState;
import lista4.gameLogic.PlayerColor;
import lista4.gameLogic.Stone;

/**
 * An implementation of {@link GameOutputAdapter} designed for GUI clients.
 * <p>
 * Unlike the console adapter, this class does not send visual text
 * representations.
 * Instead, it sends <b>structured protocol commands</b> that the client's GUI
 * parser
 * uses to update the graphical display.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Managing connection streams for GUI clients.</li>
 * <li>Sending state updates via protocol messages.</li>
 * <li>Iterating through the board and sending {@code UPDATE} commands for every
 * stone.</li>
 * </ul>
 * </p>
 */
public class GUIOutputGameAdapter implements GameOutputAdapter<String> {

    /** Map of active writers for connected GUI clients. */
    private static final ConcurrentMap<PlayerColor, PrintWriter> activeWriters = new ConcurrentHashMap<>();

    /**
     * Registers a player and their output stream.
     *
     * @param color The player's color.
     * @param out   The output stream for sending protocol commands.
     */
    public void registerPlayer(PlayerColor color, PrintWriter out) {
        activeWriters.put(color, out);
        // out.println("Welcome " + color);
        if (activeWriters.size() == 2) {
            // sendState(, color);
        }
    }

    /**
     * Sends a game state update message.
     *
     * @param gameState The current state.
     * @param target    The recipient.
     */
    @Override
    public void sendState(GameState gameState, PlayerColor target) {
        sendToTarget("STATUS: " + gameState.toString(), target);
    }

    /**
     * Unregisters a player (e.g., on disconnect).
     *
     * @param color The player to remove.
     */
    public void unregisterPlayer(PlayerColor color) {
        activeWriters.remove(color);
    }

    /**
     * Broadcasts a raw message string to all connected clients.
     *
     * @param message The message to send.
     */
    public void sendBroadcast(String message) {
        for (PrintWriter out : activeWriters.values()) {
            if (out != null) {
                out.println(message);
            }
        }
    }

    /**
     * Synchronizes the client's board view with the server's board state.
     * <p>
     * This method iterates over the entire board and calls
     * {@link #sendSpecificStoneUpdates}
     * to send individual update commands for each stone found on the board.
     * This allows the GUI to redraw or update its internal model.
     * </p>
     *
     * @param board  The current game board.
     * @param target The recipient (Specific player or BOTH).
     */
    public void sendBoard(Board board, PlayerColor target) {
        if (target == PlayerColor.BOTH) {
            for (PrintWriter out : activeWriters.values()) {
                if (out != null) {
                    sendSpecificStoneUpdates(out, board); // GUI
                }
            }
        } else {
            PrintWriter out = activeWriters.get(target);
            sendSpecificStoneUpdates(out, board); // GUI
        }
    };

    /**
     * Sends an error message to a client.
     *
     * @param exception The exception details.
     * @param target    The recipient.
     */
    public void sendExceptionMessage(Exception exception, PlayerColor target) {
        PrintWriter out = activeWriters.get(target);
        out.println(exception.getMessage());
        // out.println("blad");
    };

    /**
     * Routing helper for sending messages.
     *
     * @param message The message content.
     * @param target  The recipient.
     */
    public void sendToTarget(String message, PlayerColor target) {
        if (target == PlayerColor.BOTH) {
            sendBroadcast(message);
        } else {
            PrintWriter out = activeWriters.get(target);
            if (out != null)
                out.println(message);
        }
    }

    /**
     * Iterates through the board and sends protocol commands to update stones.
     * <p>
     * The protocol format sent to the client is:
     * {@code UPDATE [COLOR] [X] [Y]}
     * </p>
     *
     * @param out   The output stream to send commands to.
     * @param board The board data to serialize into commands.
     */
    private void sendSpecificStoneUpdates(PrintWriter out, Board board) {
        // Najpierw czyścimy widok u klienta (opcjonalnie, zależy od logiki GUI)
        // out.println("CLEAR_BOARD");

        for (int y = 0; y < board.getSize(); y++) {
            for (int x = 0; x < board.getSize(); x++) {
                Stone stone = board.getStone(x, y);
                if (stone != null) {
                    String color = (stone.getPlayerColor() == PlayerColor.WHITE) ? "WHITE" : "BLACK";
                    // Format: UPDATE [COLOR] [X] [Y] wysyła do GUI aby wiedziało jak kolorować
                    out.println("UPDATE " + color + " " + x + " " + y);
                } else {
                    out.println("UPDATE BLANK " + x + " " + y);
                }
            }
        }
    }

    /**
     * Broadcasts the final result of the game.
     *
     * @param playerColor The winner of the game.
     * @param whiteStones The total score of the White player.
     * @param blackStones The total score of the Black player.
     * @param byGivingUp  True if the game ended by resignation.
     */
    public void sendWiningMassage(PlayerColor playerColor, int whiteStones, int blackStones, boolean byGivingUp) {
        sendBroadcast(playerColor + " wygrał.");
    }

    /**
     * Notifies players about whose turn it is via a STATUS command.
     *
     * @param playerColor The color of the active player.
     */
    public void sendCurrentPlayer(PlayerColor playerColor) {
        sendBroadcast("STATUS: " + playerColor);

    }

    /**
     * Signals the start of the negotiation/territory phase.
     */
    public void sendNegotiationStart() {
        sendBroadcast("NEGOTIATION");
    }

    /**
     * Signals that the negotiation phase has ended or a proposition has been made.
     *
     * @param playerColor The player associated with the event.
     */
    public void sendEndOfNegotiationToPlayer(PlayerColor playerColor) {
        sendBroadcast("NEGOTIATION PROPOSITION");

    }

    /**
     * Sends an update about a specific territory proposal (e.g. marking a group as
     * dead).
     *
     * @param x           The X coordinate.
     * @param y           The Y coordinate.
     * @param playerColor The player proposing the change.
     * @param update_type The type of update (e.g. add/remove marker).
     */
    public void sendTeritoryUpdate(int x, int y, PlayerColor playerColor, String update_type) {
        sendBroadcast("REC_PROP " + playerColor + " " + x + " " + y + " " + update_type);
    }

    /**
     * Broadcasts the number of captured stones for both sides.
     *
     * @param blackCaptured Total stones captured by Black.
     * @param whiteCaptured Total stones captured by White.
     */
    public void sendCaptureStonesQuantity(int blackCaptured, int whiteCaptured) {
        sendBroadcast("black captured stones: " + blackCaptured + " white captured stones: " + whiteCaptured);
    }

    /**
     * Resumes the game (usually after a rejected negotiation), refreshing the board
     * for all.
     *
     * @param board The current game board.
     */
    public void resumeGame(Board board) {
        sendBoard(board, PlayerColor.BOTH);
    }

    public void sendGamesList(List<GameEntity> gamesList, PlayerColor playerColor) {
        for (GameEntity game : gamesList) {
            sendToTarget("SAVEDGAME " + game.getId() + " " + game.getStartTime(), playerColor);
        }

    }
}