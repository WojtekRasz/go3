package lista4.backend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;

import lista4.gameLogic.GameManager;
import lista4.gameLogic.PlayerColor;
import lista4.gameInterface.GameInputAdapter;
import lista4.gameInterface.GameOutputAdapter;
import lista4.adapters.InputGameAdapter;
import lista4.adapters.OutputGameAdapter;
import lista4.adapters.GUIInputGameAdapter;
import lista4.adapters.GUIOutputGameAdapter;

/**
 * The main entry point for the Game Server.
 * <p>
 * This class is responsible for initializing the backend infrastructure of the
 * game.
 * Its primary responsibilities include:
 * <ul>
 * <li>Creating and maintaining the singleton instance of
 * {@link GameManager}.</li>
 * <li>Initializing communication adapters for both Console and GUI
 * clients.</li>
 * <li>Listening for incoming client connections on a specific port.</li>
 * <li>Managing a thread pool to handle multiple client connections
 * simultaneously.</li>
 * <li>Assigning {@link PlayerColor} (Black/White) to connecting players and
 * starting the game when the lobby is full.</li>
 * </ul>
 * </p>
 */
public class Server {

    /** The maximum number of players allowed in a single game session. */
    private static int GAMERS_NUMBER = 2;

    /** The network port on which the server listens for connections. */
    private static final int PORT = 12345;

    /**
     * A list tracking the currently connected players to prevent duplicate roles.
     */
    private static ArrayList<PlayerColor> gamers = new ArrayList<>();

    /** The singleton instance of the game logic manager. */
    public static GameManager gameManager = GameManager.getInstance();

    // -- Adapters Initialization --

    /** Output adapter for Console clients. */
    private static GameOutputAdapter outAdapter = new OutputGameAdapter();

    /** Input adapter for Console clients. */
    private static GameInputAdapter inAdapter = new InputGameAdapter(gameManager, outAdapter);

    /** Output adapter for GUI clients. */
    private static GameOutputAdapter GUIoutAdapter = new GUIOutputGameAdapter();

    /** Input adapter for GUI clients. */
    private static GameInputAdapter GUIinAdapter = new GUIInputGameAdapter(gameManager, GUIoutAdapter);

    /**
     * The main method that starts the server.
     * <p>
     * It performs the following steps:
     * <ol>
     * <li>Prepares lists of input/output adapters to be passed to client
     * threads.</li>
     * <li>Creates a fixed thread pool based on {@code GAMERS_NUMBER}.</li>
     * <li>Opens a {@link ServerSocket} and enters an infinite loop to accept
     * connections.</li>
     * <li>When a client connects:
     * <ul>
     * <li>If the BLACK slot is empty, assigns BLACK and starts a new
     * {@link ClientThread}.</li>
     * <li>If the WHITE slot is empty, assigns WHITE, starts a new
     * {@link ClientThread}, and triggers {@code gameManager.startGame()}.</li>
     * </ul>
     * </li>
     * </ol>
     * </p>
     *
     * @param args Command line arguments (not used).
     * @throws IOException If the server cannot bind to the specified port or accept
     *                     connections.
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Wielowątkowy serwer jest uruchomiony na porcie " + PORT + "...");

        // Prepare adapter lists to allow ClientThread to switch between Console/GUI
        ArrayList<GameInputAdapter> inputAdapters = new ArrayList<>();
        ArrayList<GameOutputAdapter> outputAdapters = new ArrayList<>();

        inputAdapters.add(GUIinAdapter);
        inputAdapters.add(inAdapter);
        outputAdapters.add(GUIoutAdapter);
        outputAdapters.add(outAdapter);

        ExecutorService pool = Executors.newFixedThreadPool(GAMERS_NUMBER);

        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = listener.accept();
                System.out.println(">> Połączono z klientem: " + clientSocket.getInetAddress());

                if (gamers.size() < 2 && !gamers.contains(PlayerColor.BLACK)) {
                    // Assign Black Player
                    pool.execute(new ClientThread(clientSocket, inputAdapters,
                            outputAdapters, PlayerColor.BLACK, gamers));
                    gamers.add(PlayerColor.BLACK);

                } else if (gamers.size() < 2 && !gamers.contains(PlayerColor.WHITE)) {
                    // Assign White Player and Start Game
                    pool.execute(new ClientThread(clientSocket, inputAdapters,
                            outputAdapters, PlayerColor.WHITE, gamers));
                    gamers.add(PlayerColor.WHITE);
                    gameManager.startGame();
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}