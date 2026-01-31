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
 * An implementation of {@link GameOutputAdapter} designed for Console-based
 * clients.
 * <p>
 * This adapter is responsible for:
 * <ul>
 * <li>Managing active output streams (PrintWriters) for connected players.</li>
 * <li>Converting the game board into a visual ASCII text representation.</li>
 * <li>Broadcasting game state messages and errors in a human-readable text
 * format.</li>
 * </ul>
 * It uses a {@link ConcurrentHashMap} to ensure thread safety when accessing
 * player streams.
 * </p>
 */
public class OutputGameAdapter implements GameOutputAdapter<String> {

    /** A thread-safe map associating player colors with their output streams. */
    private static final ConcurrentMap<PlayerColor, PrintWriter> activeWriters = new ConcurrentHashMap<>();

    /**
     * Registers a player's output stream, enabling the server to send messages to
     * this client.
     *
     * @param color The color of the player registering.
     * @param out   The {@link PrintWriter} connected to the client's socket.
     */
    public void registerPlayer(PlayerColor color, PrintWriter out) {
        activeWriters.put(color, out);
    }

    /**
     * Sends a text-based status update regarding the current game state.
     *
     * @param gameState The current state of the game (e.g., WHOSE_TURN).
     * @param target    The recipient of the message (Specific player or BOTH).
     */
    @Override
    public void sendState(GameState gameState, PlayerColor target) {
        sendToTarget("STATUS: " + gameState.toString(), target);
    }

    /**
     * Removes a player from the active writers map, typically upon disconnection.
     *
     * @param color The color of the player to unregister.
     */
    public void unregisterPlayer(PlayerColor color) {
        activeWriters.remove(color);
    }

    /**
     * Sends a raw message to all currently registered players.
     *
     * @param message The string message to broadcast.
     */
    public void sendBroadcast(String message) {
        for (PrintWriter out : activeWriters.values()) {
            if (out != null) {
                out.println(message);
            }
        }
    }

    /**
     * Generates an ASCII representation of the board and sends it to the target(s).
     * <p>
     * This method converts the {@link Board} object into a formatted string grid
     * (with coordinates and stone markers) suitable for display in a terminal.
     * </p>
     *
     * @param board  The current game board.
     * @param target The recipient (Specific player or BOTH).
     */
    public void sendBoard(Board board, PlayerColor target) {
        if (target == PlayerColor.BOTH) {
            for (PrintWriter out : activeWriters.values()) {
                if (out != null) {
                    out.println(boardToString(board));
                }
            }
        } else {
            PrintWriter out = activeWriters.get(target);
            out.println(boardToString(board));
        }
    };

    /**
     * Sends an exception message to a specific player to indicate an error (e.g.,
     * illegal move).
     *
     * @param exception The exception that occurred.
     * @param target    The player who caused or needs to know about the error.
     */
    public void sendExceptionMessage(Exception exception, PlayerColor target) {
        PrintWriter out = activeWriters.get(target);
        out.println(exception.getMessage());
        out.println("blad");
    };

    /**
     * Helper method that creates a String representation of the board.
     *
     * @param board The board to convert.
     * @return A multi-line string representing the grid, coordinates, and stones.
     */
    private String boardToString(Board board) {
        String result = "   A  B  C  D  E  F  G  H  I  J  K  L  M  N  O  P  Q  R  S";
        for (int y = 0; y < board.getSize(); y++) {
            result = result.concat("\n");
            result = result.concat(String.format("%2d", y + 1) + "");
            for (int x = 0; x < board.getSize(); x++) {
                Stone stone = board.getStone(x, y);
                if (stone == null) {
                    result = result.concat(" . ");
                } else if (stone.getPlayerColor() == PlayerColor.WHITE) {
                    result = result.concat(" W ");
                } else {
                    result = result.concat(" B ");
                }
            }
        }
        return result;
    }

    /**
     * Internal helper to route a message to one or all players.
     *
     * @param message The message to send.
     * @param target  The recipient (Specific player or BOTH).
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
     * Broadcasts the final result of the game to all players.
     *
     * Determines the message based on whether the game ended by resignation or
     * by counting territories/stones.
     *
     * @param playerColor The winner of the game.
     * @param whiteStones The total score of the White player.
     * @param blackStones The total score of the Black player.
     * @param byGivingUp  True if the game ended via resignation.
     */
    public void sendWiningMassage(PlayerColor playerColor, int whiteStones, int blackStones, boolean byGivingUp) {
        sendBroadcast("Zwyciężył " + playerColor);
    }

    /**
     * Notifies all players about whose turn it is.
     *
     * @param playerColor The color of the active player.
     */
    public void sendCurrentPlayer(PlayerColor playerColor) {
        sendBroadcast("STATUS: " + playerColor);

    }

    /**
     * Broadcasts a signal to start the negotiation phase.
     *
     * This informs clients to switch from gameplay mode to territory selection
     * mode.
     */
    public void sendNegotiationStart() {
        sendBroadcast("NEGOTIATION");
    }

    /**
     * Notifies players that the negotiation phase has ended or a proposal
     * has been finalized.
     *
     * @param playerColor The player associated with the end of negotiation event.
     */
    public void sendEndOfNegotiationToPlayer(PlayerColor playerColor) {
        sendBroadcast("NEGOTIATION ENDED");
    }

    /**
     * Sends a specific update regarding a territory proposal on the board.
     *
     * Used to synchronize the visual markers (e.g., green squares) on clients.
     *
     * @param x           The X coordinate of the field.
     * @param y           The Y coordinate of the field.
     * @param playerColor The color claiming the territory.
     * @param update_type The type of update ("+" to add, "-" to remove).
     */
    public void sendTeritoryUpdate(int x, int y, PlayerColor playerColor, String update_type) {
        sendBroadcast("REC_PROP " + playerColor + " " + x + " " + y + " " + update_type);

    }

    /**
     * Broadcasts the current count of stones captured by each player.
     *
     * @param blackCaptured Total stones captured by Black.
     * @param whiteCaptured Total stones captured by White.
     */
    public void sendCaptureStonesQuantity(int blackCaptured, int whiteCaptured) {
        sendBroadcast("black captured stones: " + blackCaptured + " white captured stones: " + whiteCaptured);

    }

    /**
     * Resumes the game from the negotiation phase, typically if a proposal was
     * rejected.
     *
     * This re-sends the board state to ensure all clients have the correct view
     * before continuing gameplay.
     *
     * @param board The current state of the board.
     */
    public void resumeGame(Board board) {
        sendBoard(board, PlayerColor.BOTH);
    }

    /**
     * Sends list of games to the user who asked
     * 
     * @param gamesList   - list of params from backend
     * @param playerColor - player who will receive the list of games
     */
    public void sendGamesList(List<GameEntity> gamesList, PlayerColor playerColor) {
        System.out.println(gamesList);
        for (GameEntity game : gamesList) {
            System.out.println(game);
        }

    }
}
