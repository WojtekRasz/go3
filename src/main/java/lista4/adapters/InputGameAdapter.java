
package lista4.adapters;

import lista4.gameInterface.GameInputAdapter;
import lista4.gameInterface.GameOutputAdapter;
import lista4.gameInterface.IOExceptions.WrongMoveFormat;
import lista4.gameLogic.GameManager;
import lista4.gameLogic.PlayerColor;
import lista4.gameLogic.Move;

/**
 * An implementation of {@link GameInputAdapter} tailored for Console/Terminal
 * clients.
 * <p>
 * This class handles text-based input from the standard console. It interprets
 * typed commands and converts them into game moves.
 * </p>
 * <p>
 * Key features:
 * <ul>
 * <li>Parses standard algebraic notation (e.g., "D 16") into board
 * indices.</li>
 * <li>Provides human-readable error messages suitable for console display.</li>
 * <li>Updates the {@link GameManager} with the appropriate console output
 * adapter.</li>
 * </ul>
 * </p>
 */
public class InputGameAdapter implements GameInputAdapter<String> {

    /** The core game logic controller. */
    private final GameManager gameManager;

    /**
     * Constructs the console input adapter.
     *
     * @param gameManager The singleton instance of the game logic.
     * @param outAdapter  The output adapter associated with this input channel.
     */
    public InputGameAdapter(GameManager gameManager, GameOutputAdapter outAdapter) {
        this.gameManager = gameManager;
    }

    /**
     * Switches the Game Manager's output channel to the Console adapter.
     * <p>
     * This is critical when switching control between different interface types
     * to ensure the game prints text responses instead of GUI commands.
     * </p>
     *
     * @param outAdapter The {@link GameOutputAdapter} to set.
     */
    public void setOutAdapter(GameOutputAdapter outAdapter) {
        this.gameManager.setAdapter(outAdapter);
    }

    /**
     * Interprets a text command as a game move.
     * <p>
     * Accepted format: <code>[A-S] [1-19]</code> (case insensitive).<br>
     * Example: "a 1" translates to (0,0). "s 19" translates to (18,18).
     * </p>
     *
     * @param input The text string typed by the user.
     * @param color The color of the player making the move.
     * @throws WrongMoveFormat If the command syntax is incorrect (e.g., out of
     *                         bounds or wrong format).
     */
    public void makeMove(String input, PlayerColor color) throws WrongMoveFormat {
        if (input.matches("[a-sA-S] [1-9]") || input.matches("[a-sA-S] 1[0-9]")) {
            int base = (int) 'a';
            int y = Integer.parseInt(input.substring(2)) - 1;
            int x = (int) input.toLowerCase().charAt(0) - base;

            gameManager.makeMove(new Move(x, y, color));
        } else {
            throw new WrongMoveFormat("zła komenda podaj w formacie A-S 1-19");
        }
    };

    /**
     * Triggers the sending of the ASCII representation of the board.
     *
     * @param color The player for whom the board is being refreshed.
     */
    public void sendBoardRequest(PlayerColor color) {
        gameManager.sendBoard(color);
    };

    /**
     * Handles commands related to proposing territory changes during negotiation.
     *
     * Parses commands starting with "PROP".
     * Format: "PROP + [coord]" to add, "PROP - [coord]" to remove.
     *
     * @param input The raw input string.
     * @param color The player sending the proposal.
     */
    public void sendChangingTeritory(String input, PlayerColor color) {
        if (input.matches("PROP [+-] [a-sA-S] [1-9]") || input.matches("PROP [+-] [a-sA-S] 1[0-9]")) {
            String[] parts = input.split(" ");
            String sign = parts[1];
            int base = (int) 'a';
            int y = Integer.parseInt(input.substring(9)) - 1;
            int x = (int) input.toLowerCase().charAt(7) - base;
            if (sign.equals("+")) {
                gameManager.addTerritory(color, x, y);
            } else if (sign.equals("-")) {
                gameManager.removeTerritory(color, x, y);
            }
            System.out.println("wysłano");
        } else {
            throw new WrongMoveFormat(
                    "zła komenda podaj w formacie \"PROP -/+ A-S 1-19\" (+ - oznacza że chcesz dodać, - usunąć propozycje)");
        }
    }

    /**
     * Signals that the player wishes to pass their turn.
     *
     * @param color The player passing.
     */
    public void sendPass(PlayerColor color) {
        gameManager.passMove(color);
    }

    /**
     * Signals that the player wishes to resign (give up) the game.
     *
     * @param color The player resigning.
     */
    public void sendGiveUp(PlayerColor color) {
        gameManager.giveUpGame(color);
    }

    /**
     * Signals a request to resume the game (typically rejecting a negotiation).
     *
     * @param color The player requesting to resume.
     */
    public void sendResumeGame(PlayerColor color) {
        gameManager.resumeGame(color);
    }

    /**
     * Proposes to finish the negotiation phase (submit current proposal).
     *
     * @param color The player submitting the proposal.
     */
    public void proposeFinishNegotiation(PlayerColor color) {
        gameManager.proposeFinishNegotiation(color);
    }

    /**
     * Accepts the opponent's negotiation proposal, finalizing the game.
     *
     * @param color The player accepting the proposal.
     */
    public void acceptFinishNegotiation(PlayerColor color) {
        gameManager.finishNegotiation(color);
    }

    /**
     * Send request to load games from database
     * <p>
     * This will send a list of games to frontend
     * </p>
     *
     * @param color The color of the player who send a request.
     */
    public void sendGamesList(PlayerColor color) {
        gameManager.sendGameList(color);
    };

    /**
     * <p>
     * Will reload a board to a game with the id in argument
     * </p>
     *
     * @param gameId the id of the game which will be loaded.
     */
    public void loadGameById(Long gameId) {
        gameManager.loadGameById(gameId);
    };

}
