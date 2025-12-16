package lista4.gameInterface;

import lista4.gameLogic.PlayerColor;
import lista4.gameInterface.IOExceptions.WrongMoveFormat;

public interface GameInputAdapter<InputType> {

    /**
     * checks if input is valid and send it or throw error
     * 
     * @param input
     * @return
     * @throws WrongMoveFormat
     */
    void makeMove(String input, PlayerColor color) throws WrongMoveFormat;

}
