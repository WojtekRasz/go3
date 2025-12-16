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
import lista4.gameInterface.IOExceptions.WrongMoveFormat;

/**
 * Class responsible for communication between Client and the game.
 * It is runnig as thread in server - now can be only 2 ClientThreads
 * It (should) send moves and gives answare to Client
 */
class ClientThread implements Runnable {
    private Socket socket;
    GameInputAdapter inAdapter;
    GameOutputAdapter outAdapter;
    PlayerColor color;
    ArrayList gamers;

    ClientThread(Socket socket, GameInputAdapter inAdapter, GameOutputAdapter outAdapter,
            PlayerColor color, ArrayList gamers) {
        this.socket = socket;
        this.inAdapter = inAdapter;
        this.outAdapter = outAdapter;
        this.color = color;
        this.gamers = gamers;
    }

    @Override
    public void run() {

        try (Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            outAdapter.registerPlayer(color, out);
            out.println("WELCOME " + color + ". Waiting for command.");
            while (in.hasNextLine()) {
                String clientMessage = in.nextLine();
                // System.out.println(" [Klient " + color + "]: " + clientMessage);

                // send move to INadapter and wait for a message from Outadapter
                // based on input try catch inAdaper
                try {
                    inAdapter.makeMove(clientMessage, color);
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
                socket.close();
                System.out.println(">> Połączenie zakończone z klientem: " + socket.getInetAddress());
            } catch (IOException e) {
            }
        }
    }
}