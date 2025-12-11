package lista4.gameInterface;

import lista4.gameLogic.Board;

public interface GameOutputAdapter<OutputType> {

    void sendBoard(Board board);
    //void sendStatus(...);
    void sendExceptionMessage(Exception exception);

}
