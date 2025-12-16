package lista4.adapters;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lista4.gameInterface.GameOutputAdapter;
import lista4.gameLogic.Board;
import lista4.gameLogic.GameManager;
import lista4.gameLogic.gameExceptions.OutputException;
import lista4.gameLogic.state.GameState;

/**
 * Class uses Observers (ClientThread) to communicate with client
 * It is use in Game to send
 */
public class OutputGameAdapter implements GameOutputAdapter<String> {
    private static final ConcurrentMap<GameManager.PlayerColor, PrintWriter> activeWriters = new ConcurrentHashMap<>();

    public void registerPlayer(GameManager.PlayerColor color, PrintWriter out) {
        activeWriters.put(color, out);
    }

    @Override
    public void sendState(GameState gameState, GameManager.PlayerColor target) {

    }

    @Override
    public void sendExceptionMessage(OutputException exception, GameManager.PlayerColor target) {

    }

    public void unregisterPlayer(GameManager.PlayerColor color) {
        activeWriters.remove(color);
    }

    public void sendBroadcast(String message) {
        for (PrintWriter out : activeWriters.values()) {
            if (out != null) {
                out.println("message");
            }
        }
    }

    public void sendBoard(Board board, String mes, GameManager.PlayerColor target) { // wysyła na razie zwykłego stringa
        for (PrintWriter out : activeWriters.values()) {
            if (out != null) {
                out.println("message: " + mes);
            }
        }
    };

}
