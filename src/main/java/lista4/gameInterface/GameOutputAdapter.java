package lista4.gameInterface;

import java.io.PrintWriter;

import lista4.gameLogic.Board;
import lista4.gameLogic.gameExceptions.OutputException;
import lista4.gameLogic.state.GameState;
import lista4.gameLogic.PlayerColor;

// będzie miało observerów
public interface GameOutputAdapter<OutputType> {

    void sendBoard(Board board, PlayerColor target);

    void sendBroadcast(String message);

    void registerPlayer(PlayerColor color, PrintWriter out); // to dodałem bo client thread używał i był
                                                             // błąd bo po
    // prostu tego brakowało(tam
    // widać dlaczego)

    void sendState(GameState gameState, PlayerColor target);

    void sendExceptionMessage(Exception exception, PlayerColor target);

}
