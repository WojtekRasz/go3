package lista4.frontend;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * A command-line interface (CLI) client for the Go game.
 * <p>
 * This class establishes a TCP connection to the game server and handles
 * text-based
 * interaction. It utilizes a separate thread to listen for server messages
 * asynchronously
 * while the main thread waits for user input.
 * </p>
 * <p>
 * Workflow:
 * <ol>
 * <li>Connects to the server at {@code 127.0.0.1:12345}.</li>
 * <li>Starts a {@link ServerListener} thread to print incoming messages.</li>
 * <li>Sends the "console" handshake to identify itself to the server.</li>
 * <li>Enters a loop reading from standard input and sending commands to the
 * server.</li>
 * </ol>
 * </p>
 */
public class Client {

    /** The server IP address. */
    private static final String SERVER_ADDRESS = "127.0.0.1";

    /** The server port. */
    private static final int PORT = 12345;

    /**
     * The main entry point for the console client.
     * * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        System.out.println("Łączenie z serwerem " + SERVER_ADDRESS + ":" + PORT + "...");

        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {

            // Start the listening thread
            Thread listenerThread = new Thread(new ServerListener(socket));
            listenerThread.start();

            System.out.print(">");
            String lineToSend;

            // Handshake: Identify as console client to receive ASCII board
            out.println("console");

            // Main Input Loop
            while ((lineToSend = consoleIn.readLine()) != null) {
                out.println(lineToSend);
                // System.out.print(">");
            }

        } catch (IOException e) {
            System.err.println("Błąd połączenia/komunikacji: " + e.getMessage());
        }
        System.out.println("Klient zakończył działanie.");
    }

    /**
     * A helper class responsible for listening to incoming server messages.
     * <p>
     * It runs in a separate thread to prevent blocking the user's input loop.
     * </p>
     */
    public static class ServerListener implements Runnable {
        private final Socket socket;

        /**
         * Creates a listener for the specified socket.
         * 
         * @param socket The active connection to the server.
         */
        public ServerListener(Socket socket) {
            this.socket = socket;
        }

        /**
         * Continuously reads lines from the server input stream and prints them to the
         * console.
         * Stops if "GOODBYE" is received or the connection is closed.
         */
        @Override
        public void run() {
            try (Scanner in = new Scanner(socket.getInputStream())) {

                while (in.hasNextLine()) {
                    String message = in.nextLine();
                    System.out.println(message);
                    if (message.contains("GOODBYE")) {
                        break;
                    }
                }
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    System.err.println("ListenerThread: Błąd odczytu: " + e.getMessage());
                }
            } finally {
                try {
                    if (!socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException e) {
                    // Ignore close errors
                }
            }
        }
    }
}