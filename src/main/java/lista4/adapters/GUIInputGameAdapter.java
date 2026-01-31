package lista4.adapters;

import lista4.gameInterface.GameInputAdapter;
import lista4.gameInterface.GameOutputAdapter;
import lista4.gameInterface.IOExceptions.WrongMoveFormat;
import lista4.gameLogic.GameManager;
import lista4.gameLogic.PlayerColor;
import lista4.gameLogic.Move;

/**
 * An implementation of {@link GameInputAdapter} tailored for GUI clients.
 * <p>
 * This class acts as a bridge between the raw string commands received from a
 * JavaFX GUI client and the internal logic of the {@link GameManager}.
 * Its main responsibilities are:
 * <ul>
 * <li>Validating the format of incoming move commands using Regex.</li>
 * <li>Parsing negotiation commands (adding/removing territory proposals).</li>
 * <li>Translating string coordinates (e.g., "A 1") into integer grid
 * coordinates (x=0, y=0).</li>
 * <li>Delegating valid actions (moves, passes, resignations, negotiations) to
 * the {@link GameManager}.</li>
 * </ul>
 * </p>
 */
public class GUIInputGameAdapter implements GameInputAdapter<String> {

    /** The core game logic controller. */
    private final GameManager gameManager;

    /**
     * Constructs the adapter with a reference to the game manager.
     *
     * @param gameManager The singleton instance of the game logic.
     * @param outAdapter  The output adapter (unused in constructor, but available
     *                    for reference).
     */
    public GUIInputGameAdapter(GameManager gameManager, GameOutputAdapter outAdapter) {
        this.gameManager = gameManager;
    }

    /**
     * Updates the output adapter used by the GameManager.
     * <p>
     * This method ensures that the game logic sends responses to the correct
     * output channel (the GUI output adapter in this context).
     * </p>
     *
     * @param outAdapter The new {@link GameOutputAdapter} to be used by the game.
     */
    public void setOutAdapter(GameOutputAdapter outAdapter) {
        this.gameManager.setAdapter(outAdapter);
    }

    /**
     * Processes a move command received from the GUI.
     * <p>
     * The method expects a string in the format <code>"[Letter] [Number]"</code>
     * (e.g., "C 5"). It converts the letter to an X-coordinate (A=0, B=1...)
     * and the number to a Y-coordinate (1=0, 2=1...).
     * </p>
     *
     * @param input The raw command string from the client.
     * @param color The color of the player attempting the move.
     * @throws WrongMoveFormat If the input string does not match the expected
     *                         coordinate format.
     */
    public void makeMove(String input, PlayerColor color) throws WrongMoveFormat {
        // Regex validates ranges: A-S (case insensitive) and 1-19
        if (input.matches("[a-sA-S] [1-9]") || input.matches("[a-sA-S] 1[0-9]")) {
            int base = (int) 'a';
            // Parse Y: "1" becomes index 0
            int y = Integer.parseInt(input.substring(2)) - 1;
            // Parse X: 'a'/'A' becomes index 0
            int x = (int) input.toLowerCase().charAt(0) - base;

            gameManager.makeMove(new Move(x, y, color));
        } else {
            throw new WrongMoveFormat("Błąd wysyłania ruchu przez GUI.");
        }
    };

    /**
     * Requests the game manager to broadcast the current board state.
     *
     * @param color The player requesting the board update (or responsible for the
     *              trigger).
     */
    public void sendBoardRequest(PlayerColor color) {
        gameManager.sendBoard(color);
    };

    /**
     * Processes a territory negotiation command during the scoring phase.
     * <p>
     * Expects a command in the format: <code>"PROP [+/-] [Letter] [Number]"</code>.
     * <ul>
     * <li><b>+</b>: Proposes marking a stone group as dead (or territory as
     * owned).</li>
     * <li><b>-</b>: Removes a previous proposal.</li>
     * </ul>
     * </p>
     *
     * @param input The raw command string (e.g., "PROP + A 10").
     * @param color The color of the player making the proposal.
     * @throws WrongMoveFormat If the command format is invalid.
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
     * @param color The color of the player passing.
     */
    public void sendPass(PlayerColor color) {
        gameManager.passMove(color);
    }

    /**
     * Signals that the player wishes to resign (give up) the game.
     *
     * @param color The color of the player resigning.
     */
    public void sendGiveUp(PlayerColor color) {
        gameManager.giveUpGame(color);
    }

    /**
     * Signals that a player rejects the current territory proposal and wishes to
     * resume gameplay.
     * <p>
     * This moves the game state from NEGOTIATIONS back to RUNNING.
     * </p>
     *
     * @param color The color of the player resuming the game.
     */
    public void sendResumeGame(PlayerColor color) {
        gameManager.resumeGame(color);
    }

    /**
     * Signals that a player accepts the current territory arrangement.
     * <p>
     * If this is the first player to accept, the game waits for the second player.
     * </p>
     *
     * @param color The color of the player accepting the proposal.
     */
    public void proposeFinishNegotiation(PlayerColor color) {
        gameManager.proposeFinishNegotiation(color);
    }

    /**
     * Signals that the second player has accepted the arrangement.
     * <p>
     * This finalizes the negotiations and triggers the end of the game and score
     * calculation.
     * </p>
     *
     * @param color The color of the player finalizing the negotiation.
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
