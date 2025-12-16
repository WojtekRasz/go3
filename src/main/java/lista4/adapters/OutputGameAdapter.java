package lista4.adapters;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lista4.gameInterface.GameOutputAdapter;
import lista4.gameLogic.Board;
import lista4.gameLogic.state.GameState;
import lista4.gameLogic.PlayerColor;
import lista4.gameLogic.Stone;

/**
 * Class uses Observers (ClientThread) to communicate with client
 * It is use in Game to send
 */
public class OutputGameAdapter implements GameOutputAdapter<String> {
    private static final ConcurrentMap<PlayerColor, PrintWriter> activeWriters = new ConcurrentHashMap<>();

    public void registerPlayer(PlayerColor color, PrintWriter out) {
        activeWriters.put(color, out);
    }

    @Override
    public void sendState(GameState gameState, PlayerColor target) {

    }

    public void unregisterPlayer(PlayerColor color) {
        activeWriters.remove(color);
    }

    public void sendBroadcast(String message) {
        for (PrintWriter out : activeWriters.values()) {
            if (out != null) {
                out.println(message);
            }
        }
    }

    public void sendBoard(Board board, PlayerColor target) { // wysyła na razie zwykłego stringa
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

    public void sendExceptionMessage(Exception exception, PlayerColor target) {
        PrintWriter out = activeWriters.get(target);
        out.println(exception.getMessage());
        out.println("blad");
    };

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
}
