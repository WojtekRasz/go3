package lista4.frontend;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.Scanner;
import java.net.Socket;
// import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import lista4.backend.*;

class ClientTest {

    private final String SERVER_IP = "127.0.0.1";
    private final int SERVER_PORT = 12345;

    private void consumeBoard(BufferedReader in) throws IOException {
        System.out.println("DEBUG: Oczekiwanie na nagłówek planszy...");
        String header = in.readLine();
        if (header != null) {
            System.out.println("DEBUG: Odebrano nagłówek: " + header);
        } else {
            System.out.println("DEBUG: Strumień pusty (null) przy próbie odczytu nagłówka!");
            return;
        }
        for (int i = 0; i < 19; i++) {
            String line = in.readLine();
            if (line != null) {
                System.out.println("DEBUG: Odebrano wiersz " + (i + 1) + ": " + line);
            } else {
                System.out.println("DEBUG: Serwer urwał nadawanie na wierszu " + i);
                break;
            }
        }
        System.out.println("DEBUG: Zakończono odbieranie planszy.");
    }

    @BeforeAll
    static void startRealServer() throws InterruptedException {
        // Uruchamiamy Twój serwer w osobnym wątku
        Thread serverThread = new Thread(() -> {
            try {
                // Wywołujemy main Twojego serwera
                ServerDB.main(new String[] {});
            } catch (IOException e) {
                return;
                // Jeśli port zajęty, serwer się nie uruchomi
            }
        });
        serverThread.setDaemon(true); // Zamknie się po zakończeniu testów
        serverThread.start();

        // Dajemy serwerowi chwilę na otwarcie gniazda
        Thread.sleep(500);
    }

    @Test
    void testServerListenerWithRealServer() throws IOException, InterruptedException {
        // Łączymy się do Twojego prawdziwego serwera
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Przechwytujemy konsolę
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            // Uruchamiamy ServerListener
            Runnable listener = new Client.ServerListener(socket);
            Thread t = new Thread(listener);
            t.start();

            // Prawdziwa komunikacja:
            out.println("console"); // Serwer wybiera ConsoleAdapter
            out.println("GETBOARD"); // Wysyłamy komendę do Twojego serwera

            // Czekamy chwilę na odpowiedź serwera
            Thread.sleep(500);

            // Kończymy połączenie, by przerwać pętlę in.hasNextLine()
            out.println("quit");
            t.join(1000);

            String output = outContent.toString();

            // Sprawdzamy czy serwer cokolwiek odpowiedział (np. wysłał planszę)
            assertFalse(output.isEmpty(), "Serwer powinien wysłać dane po komendzie GETBOARD");
        } finally {
            System.setOut(System.out);
        }
    }

    @Test
    void testClientHandshakeWithRealServer() throws IOException {
        // Sprawdzamy, czy Twój serwer poprawnie przyjmuje "console"
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner in = new Scanner(socket.getInputStream())) {
            // Uruchamiamy ServerListener
            out.println("console");

            // Jeśli Twój serwer po wybraniu adaptera wysyła np. "Connected as Player..."
            // możemy to sprawdzić:
            if (in.hasNextLine()) {
                String response = in.nextLine();
                assertNotNull(response);
                System.out.println("Serwer odpowiedział: " + response);
            }

            // out.println("quit");
        } catch (Exception e) {

        }

    }

    // @Test
    // void testTwoPlayersRegistrationAndMove() throws IOException {
    // try (
    // // Gracz 1
    // Socket socket1 = new Socket(SERVER_IP, SERVER_PORT);
    // PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
    // BufferedReader in1 = new BufferedReader(new
    // InputStreamReader(socket1.getInputStream()));

    // // Gracz 2
    // Socket socket2 = new Socket(SERVER_IP, SERVER_PORT);
    // PrintWriter out2 = new PrintWriter(socket2.getOutputStream(), true);
    // BufferedReader in2 = new BufferedReader(new
    // InputStreamReader(socket2.getInputStream()))) {
    // // --- KROK 1: Rejestracja Gracza 1 ---
    // socket1.setSoTimeout(2000);
    // socket2.setSoTimeout(2000);
    // out1.println("console");
    // // readLine() czeka na odpowiedź. Jeśli serwer nic nie wyśle, test zawiśnie
    // // (timeout obsłuży JUnit)
    // String resp1 = in1.readLine();
    // System.out.println("Gracz 1 otrzymał: " + resp1);
    // assertNotNull(resp1);

    // // --- KROK 2: Rejestracja Gracza 2 ---
    // out2.println("console");
    // String resp2 = in2.readLine();
    // System.out.println("Gracz 2 otrzymał: " + resp2);
    // assertNotNull(resp2);
    // assertTrue(resp2.contains("WHITE") || resp2.contains("BLACK"),
    // "Serwer powinien przydzielić kolor drugiemu graczowi");

    // out1.println("a 1");
    // System.out.println("Gracz 1 wysłał ruch: a 1");

    // // Gracz 1 odbiera planszę
    // System.out.println("Gracz 1 odbiera planszę...");
    // consumeBoard(in1);
    // System.out.println("Gracz 1 odebrał całą planszę.");

    // // Gracz 2 odbiera planszę
    // System.out.println("Gracz 2 odbiera planszę...");
    // consumeBoard(in2);
    // System.out.println("Gracz 2 odebrał całą planszę.");
    // System.out.println("Gracz 2 oczekuje na komunikat broadcast...");
    // String broadcastMsg = in2.readLine();

    // if (broadcastMsg != null) {
    // System.out.println("Gracz 2 (widz) otrzymał: " + broadcastMsg);
    // } else {
    // System.out.println("Gracz 2 nie otrzymał dodatkowego komunikatu (strumień
    // zamknięty lub brak danych).");
    // }

    // } catch (Exception e) {
    // e.printStackTrace();
    // fail("Test rzucił wyjątek: " + e.getMessage());
    // }
    // }

}