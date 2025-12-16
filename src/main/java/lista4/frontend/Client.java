package lista4.frontend;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Łączenie z serwerem " + SERVER_ADDRESS + ":" + PORT + "...");

        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {
            Thread listenerThread = new Thread(new ServerListener(socket));
            listenerThread.start(); // new thread is listening
            System.out.print(">");
            String lineToSend;

            while ((lineToSend = consoleIn.readLine()) != null) {

                // this thread is sendint to server
                out.println(lineToSend);
                // System.out.print(">");
            }

        } catch (IOException e) {
            System.err.println("Błąd połączenia/komunikacji: " + e.getMessage());
        }
        System.out.println("Klient zakończył działanie.");
    }

    private static class ServerListener implements Runnable {
        private final Socket socket;

        public ServerListener(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Scanner in = new Scanner(socket.getInputStream())) {

                // listening loop
                while (in.hasNextLine()) {
                    String message = in.nextLine();
                    System.out.println(message);
                    // System.out.flush();
                    // Opcjonalnie: Dodaj logikę do przetwarzania specjalnych komunikatów
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
                }
            }
        }
    }
}