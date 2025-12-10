// import java.io.BufferedReader;
// import java.io.IOException;
// import java.io.InputStreamReader;
// import java.io.PrintWriter;
// import java.net.ServerSocket;
// import java.net.Socket;
// import java.util.Scanner;
// import java.util.concurrent.Executors;
// import java.util.concurrent.ExecutorService;

// /**
//  * A server program which accepts requests from clients to capitalize strings.
//  * Używa wirtualnych wątków (dostępne od Javy 21) do obsługi połączeń.
//  */

// // class Capitalizer implements Runnable {
// // private Socket socket;

// // Capitalizer(Socket socket) {
// // this.socket = socket;
// // }

// // @Override
// // public void run() {
// // System.out.println("Connected: " + socket); // Zastąpienie IO.println

// // // Użycie try-with-resources dla automatycznego zamknięcia zasobów
// // // 'var' jest poprawne od Javy 10
// // try (Scanner in = new Scanner(socket.getInputStream());
// // PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

// // while (in.hasNextLine()) {
// // String line = in.nextLine();
// // System.out.println("Odebrano: " + line);
// // out.println(line.toUpperCase());
// // }

// // } catch (IOException e) { // Złap konkretny wyjątek IO
// // System.err.println("Error processing socket " + socket + ": " +
// // e.getMessage());
// // } catch (Exception e) {
// // System.err.println("General error with socket " + socket + ": " +
// // e.getMessage());
// // } finally {
// // try {
// // // Konieczne, aby zamknąć gniazdo
// // socket.close();
// // } catch (IOException e) {
// // // Ignorujemy błędy przy zamykaniu
// // }
// // System.out.println("Closed: " + socket);
// // }
// // }
// // }

// public class Server {
//     private ServerSocket socket;
//     private Socket clientSocket;
//     private PrintWriter out;
//     private BufferedReader in;

//     public void start(int port) {
//         socket = new ServerSocket(port);
//         clientSocket = listener.accept();
//         out = new PrintWriter(clientSocket.getOutputStream(), true);
//         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//         String greeting = in.readLine();
//         if ("hello server".equals(greeting)) {
//             out.println("hello client");
//         } else {
//             // out.println("unrecognised greeting");
//         }
//     }

//     public void stop() {
//         in.close();
//         out.close();
//         clientSocket.close();
//         serverSocket.close();
//     }

//     public static void main(String[] args) throws IOException {
//         Server server = new Server();
//         server.start(1234);
//     }
// }

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        System.out.println("Wielowątkowy serwer Echo jest uruchomiony na porcie " + PORT + "...");

        // Używamy puli wątków do obsługi klientów.
        // Od Javy 21, Executors.newVirtualThreadPerTaskExecutor() jest zalecane.
        // Dla kompatybilności wstecznej użyjemy stałej puli wątków:
        ExecutorService pool = Executors.newFixedThreadPool(10);

        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                // listener.accept() blokuje wątek główny, czekając na połączenie
                Socket clientSocket = listener.accept();
                System.out.println(">> Połączono z klientem: " + clientSocket.getInetAddress());

                // Przypisanie obsługi klienta do nowego wątku z puli
                pool.execute(new ClientHandler(clientSocket));
            }
        } finally {
            pool.shutdown();
        }
    }
}

// Klasa obsługująca komunikację z pojedynczym klientem w osobnym wątku
class ClientHandler implements Runnable {
    private Socket socket;

    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Pętla do czytania i odsyłania wiadomości (echo)
            while (in.hasNextLine()) {
                String clientMessage = in.nextLine();
                System.out.println("   [Klient " + socket.getInetAddress() + "]: " + clientMessage);

                // Odsyłanie wiadomości z powrotem (Echo)
                out.println("ECHO: " + clientMessage);

                if (clientMessage.equalsIgnoreCase("quit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd komunikacji z klientem " + socket.getInetAddress() + ": " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println(">> Połączenie zakończone z klientem: " + socket.getInetAddress());
            } catch (IOException e) {
                // Ignorujemy błędy przy zamykaniu
            }
        }
    }
}