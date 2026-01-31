
package lista4.backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.ArrayList;

import lista4.gameInterface.GameInputAdapter;
import lista4.gameInterface.GameOutputAdapter;
import lista4.gameLogic.GameManager;
import lista4.gameLogic.PlayerColor;

/**
 * Handles the server-side communication for a single connected client.
 * <p>
 * This class runs as a separate thread for each player. It is responsible for:
 * <ul>
 * <li>Negotiating the client type (Console vs. GUI) upon connection.</li>
 * <li>Registering the player's output stream with the
 * {@link GameOutputAdapter}.</li>
 * <li>Listening for incoming commands from the client.</li>
 * <li>Forwarding game moves and requests to the {@link GameInputAdapter}.</li>
 * </ul>
 * </p>
 */
class ClientThread implements Runnable {

    /** The socket connection to the client. */
    private Socket socket;

    /** The currently active input adapter (Console or GUI). */
    GameInputAdapter inAdapter;

    /** The currently active output adapter (Console or GUI). */
    GameOutputAdapter outAdapter;

    /** List of available input adapters to choose from. */
    ArrayList<GameInputAdapter> inputAdapters = new ArrayList<>();

    /** List of available output adapters to choose from. */
    ArrayList<GameOutputAdapter> outputAdapters = new ArrayList<>();

    /** The color assigned to this player (BLACK or WHITE). */
    PlayerColor color;

    /** Reference to the shared list of active players for connection management. */
    ArrayList gamers;

    /** Reference to the gameManager to start or change the state of game. */
    GameManager gameManager;

    /**
     * Constructs a new ClientThread.
     *
     * @param socket      The socket connected to the client.
     * @param inAdapters  A list of available {@link GameInputAdapter}s (e.g., [GUI,
     *                    Console]).
     * @param outAdapters A list of available {@link GameOutputAdapter}s (e.g.,
     *                    [GUI, Console]).
     * @param color       The {@link PlayerColor} assigned to this client.
     * @param gamers      The shared list of connected players, used for cleanup on
     *                    disconnect.
     */
    ClientThread(Socket socket, ArrayList inAdapters, ArrayList outAdapters,
            PlayerColor color, ArrayList gamers, GameManager gameManager) {
        this.socket = socket;
        this.inputAdapters = inAdapters;
        this.outputAdapters = outAdapters;
        this.color = color;
        this.gamers = gamers;
        this.gameManager = gameManager;
    }

    /**
     * The main execution loop of the client thread.
     * <p>
     * The lifecycle of this method is as follows:
     * <ol>
     * <li>Initializes input and output streams.</li>
     * <li>Waits for the initial handshake message to determine client type
     * ("console" or "GUI").</li>
     * <li>Selects the appropriate adapters based on the handshake.</li>
     * <li>Registers the player's {@link PrintWriter} with the output adapter.</li>
     * <li>Enters a loop to listen for commands:
     * <ul>
     * <li>"quit": disconnects the client.</li>
     * <li>"GETBOARD": requests a board update.</li>
     * <li>Any other string is treated as a game move.</li>
     * </ul>
     * </li>
     * </ol>
     * Ensures resources are closed and the player is removed from the list upon
     * termination.
     */
    @Override
    public void run() {

        try (Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Handshake: Determine client type
            if (in.hasNextLine()) {
                String clientMessage = in.nextLine();
                switch (clientMessage) {
                    case "console":
                        this.inAdapter = inputAdapters.get(1);
                        this.outAdapter = outputAdapters.get(1);
                        inAdapter.setOutAdapter(outAdapter);
                        break;
                    case "GUI":
                        this.inAdapter = inputAdapters.get(0);
                        this.outAdapter = outputAdapters.get(0);
                        inAdapter.setOutAdapter(outAdapter);
                        break;
                    default:
                        // Default to GUI if unknown
                        this.inAdapter = inputAdapters.get(0);
                        this.outAdapter = outputAdapters.get(0);
                        inAdapter.setOutAdapter(outAdapter);
                        break;
                }
            }

            // Registration
            out.println(color);
            outAdapter.registerPlayer(color, out);
            if (gamers.size() == 2) { // if we have 2 palyers then run the game
                synchronized (gamers) {
                    System.out.println("Mamy 2 graczy! Uruchamiam grę.");
                    gameManager.startGame();
                }
            } else {
                out.println("WAIT Czekanie na drugiego gracza...");
            }
            // Command Loop
            while (in.hasNextLine()) {
                String clientMessage = in.nextLine();
                try {
                    if (clientMessage.equalsIgnoreCase("bot")) {
                        synchronized (gamers) {
                            if (gamers.size() < 2) {
                                System.out.println("Dodaję bota jako przeciwnika!");
                                PlayerColor botColor = (color == PlayerColor.BLACK) ? PlayerColor.WHITE
                                        : PlayerColor.BLACK;
                                gamers.add(botColor);
                                gameManager.activateBot(botColor);
                                out.println("BOT_READY Przeciwnik bot został dodany.");
                                gameManager.startGame();
                                System.out.println("Mamy 2 graczy! Uruchamiam grę.");
                                // Od teraz GameManager musi wiedzieć, że jeśli tura należy do botColor,
                                // to należy wywołać botService.
                            }
                        }
                        continue;
                    } else if (clientMessage.contains("LOADGAMES")) {
                        inAdapter.sendGamesList(color);
                    } else if (clientMessage.startsWith("LOADBYID")) {
                        Long id = Long.parseLong(clientMessage.split(" ")[1]);
                        inAdapter.loadGameById(id);
                    }
                    if (clientMessage.equalsIgnoreCase("quit")) {
                        break;
                    } else if (clientMessage.equals("GETBOARD")) {
                        inAdapter.sendBoardRequest(color);
                    } else if (clientMessage.equals("SKIP")) { // skip the move
                        inAdapter.sendPass(color);
                    } else if (clientMessage.equals("GIVE UP")) { // give up
                        inAdapter.sendGiveUp(color);
                    } else if (clientMessage.equals("PROPOSITION")) {
                        inAdapter.proposeFinishNegotiation(color); // Player want to end the negotiation
                    } else if (clientMessage.contains("PROP")) {
                        inAdapter.sendChangingTeritory(clientMessage, color);
                    } else if (clientMessage.equals("RESUME")) {
                        inAdapter.sendResumeGame(color); // resume the game
                    } else if (clientMessage.equals("ACCEPT")) {
                        inAdapter.acceptFinishNegotiation(color); // Player accepts the proposition
                    } else {
                        inAdapter.makeMove(clientMessage, color);
                    }
                } catch (Exception wrongmove) {
                    out.println(wrongmove.getMessage());
                }

                if (clientMessage.equalsIgnoreCase("quit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd komunikacji z klientem " + socket.getInetAddress() + ": " + e.getMessage());
        } finally {
            try {
                gamers.remove(color);
                gameManager.waitGame();
                PlayerColor botColor = (color == PlayerColor.BLACK) ? PlayerColor.WHITE
                        : PlayerColor.BLACK;
                gameManager.deActivateBot(botColor);
                socket.close();
                System.out.println(">> Połączenie zakończone z klientem: " + socket.getInetAddress());
            } catch (IOException e) {
                // Ignore errors during closing
            }
        }
    }
}
