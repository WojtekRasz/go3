package lista4.backend;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @Test
    void testServerAcceptsConnection() throws InterruptedException {
        // Uruchamiamy serwer w osobnym wątku, żeby nie blokował testu
        Thread serverThread = new Thread(() -> {
        Server server = new Server();
            try {
                server.start();
            } catch (IOException ignored) {
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Czekamy chwilę, aż serwer wstanie
        Thread.sleep(1000);

        // Próbujemy nawiązać połączenie
        try (Socket socket = new Socket("127.0.0.1", 12345);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("console");
            assertTrue(socket.isConnected(), "Serwer powinien zaakceptować połączenie.");
        } catch (IOException e) {
            fail("Serwer nie odpowiedział na porcie 12345: " + e.getMessage());
        }
    }
}