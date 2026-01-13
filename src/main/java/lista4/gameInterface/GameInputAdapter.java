package lista4.gameInterface;

import lista4.gameLogic.PlayerColor;
import lista4.gameInterface.IOExceptions.WrongMoveFormat;

/**
 * Defines the contract for handling incoming game commands.
 *
 * This interface acts as an abstraction layer between the raw input source
 * (like a console terminal or a GUI window) and the internal game logic.
 * It allows the game to function independently of how the moves are generated.
 *
 * @param <InputType> The type of the raw input (usually String for text
 *                    commands).
 */
public interface GameInputAdapter<InputType> {

    /**
     * Validates the raw input string and attempts to execute a game move.
     *
     * This method parses the input (e.g., converting "A5" to coordinates),
     * checks if the format is correct, and delegates the action to the game
     * manager.
     *
     * @param input The raw command string received from the client.
     * @param color The color of the player attempting to make the move.
     * @throws WrongMoveFormat If the input string does not match the expected
     *                         syntax.
     */
    void makeMove(String input, PlayerColor color) throws WrongMoveFormat;

    /**
     * Connects the corresponding output adapter to the game manager.
     *
     * This ensures that when an input is processed, the game logic knows
     * which output channel (Console or GUI) to use for sending the result back.
     *
     * @param outAdapter The output adapter to associate with this input channel.
     */
    void setOutAdapter(GameOutputAdapter outAdapter);

    /**
     * Triggers a request to send the current board state to the specified player.
     *
     * This is typically used when a client connects, reconnects, or needs
     * to refresh their view of the game board.
     *
     * @param color The player who needs the board update.
     */
    void sendBoardRequest(PlayerColor color);


    void sendChangingTeritory(InputType input);

    /**
     * Pass move and
     *
     * @param color Player who pass move
     */
    void sendPass(PlayerColor color);

    /**
     * Give up
     *
     * @param color Player who gives up
     */
    void sendGiveUp(PlayerColor color);
}