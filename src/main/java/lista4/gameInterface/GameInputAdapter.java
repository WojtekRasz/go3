package lista4.gameInterface;

import lista4.gameInterface.IOExceptions.WrogMoveFormat;

public interface GameInputAdapter<InputType> {

    void makeMove(InputType input) throws WrogMoveFormat;

}
