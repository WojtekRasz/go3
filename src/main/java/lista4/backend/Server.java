package lista4.backend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lista4.gameLogic.GameManager;
import lista4.gameInterface.GameInputAdapter;
import lista4.gameInterface.GameOutputAdapter;
import lista4.adapters.InputGameAdapter;
import lista4.adapters.OutputGameAdapter;

/**
 * Server creates a game and both (IN and OUT) adapters
 * creates a socket and wait for 2 ClientThreads
 */
public class Server {
    private static int GAMERS_NUMBER = 2;
    private static final int PORT = 12345;
    public static GameManager gameManager = GameManager.getInstance();

    private static GameInputAdapter inAdapter = new InputGameAdapter(gameManager);
    private static GameOutputAdapter outAdapter = new OutputGameAdapter(); // Nie wiem czy to będzie potrzebne w serwerze, ja bym to stworzył w grze

    public static void main(String[] args) throws IOException {
        gameManager.setAdapter(outAdapter);
        System.out.println("Wielowątkowy serwer jest uruchomiony na porcie " + PORT + "...");

        ExecutorService pool = Executors.newFixedThreadPool(GAMERS_NUMBER);
        int i = 0;
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                // listener.accept() blokuje wątek główny, czekając na połączenie
                Socket clientSocket = listener.accept();
                System.out.println(">> Połączono z klientem: " + clientSocket.getInetAddress());
                // przypisanie nowego socketu do wątku,
                // wątek ma mieć adaptery, a nie grę na razie sprawdzam
                if (i == 0) {
                    pool.execute(new ClientThread(clientSocket, inAdapter, outAdapter, GameManager.PlayerColor.BLACK));
                    i = i + 1;
                } else {
                    pool.execute(new ClientThread(clientSocket, inAdapter, outAdapter, GameManager.PlayerColor.WHITE));

                }
            }
        } finally {
            pool.shutdown();
        }
    }
}
