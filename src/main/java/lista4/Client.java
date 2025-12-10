package lista4;

// import java.io.BufferedReader;
// import java.io.IOException;
// import java.io.InputStreamReader;
// import java.io.PrintWriter;
// import java.net.Socket;
// import java.util.Scanner;

// public class Client {
//     private Socket clientSocket;
//     private PrintWriter out;
//     private BufferedReader in;

//     public void startConnection(String ip, int port) {
//         clientSocket = new Socket(ip, port);
//         out = new PrintWriter(clientSocket.getOutputStream(), true);
//         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//     }

//     public String sendMessage(String msg) {
//         out.println(msg);
//         String resp = in.readLine();
//         return resp;
//     }

//     public void stopConnection() {
//         in.close();
//         out.close();
//         clientSocket.close();
//     }

//     // Używamy public static, aby metoda była punktem wejścia
//     // public static void main(String[] args) throws IOException {
//     // if (args.length != 1) {
//     // System.err.println("Użycie: java Client <adres IP serwera>");
//     // return;
//     // }

//     // String serverAddress = args[0];
//     // int port = 59898;

//     // // BufferedReader do czytania z konsoli systemowej (stdin)
//     // try (BufferedReader consoleIn = new BufferedReader(new
//     // InputStreamReader(System.in));
//     // // Nawiązujemy połączenie z serwerem
//     // Socket socket = new Socket(serverAddress, port);
//     // // out do wysyłania danych do serwera (auto-flush = true)
//     // PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//     // // in do odbierania danych od serwera
//     // Scanner in = new Scanner(socket.getInputStream())) {

//     // System.out.println("Połączono z serwerem " + serverAddress + ":" + port);
//     // System.out.println("Wpisz tekst do kapitalizacji (Ctrl+D lub EXIT, aby
//     // zakończyć):");

//     // String lineToSend;

//     // // Czytanie linii z konsoli (zastępuje in.readln() / IO.readln())
//     // while ((lineToSend = consoleIn.readLine()) != null) {

//     // if (lineToSend.equalsIgnoreCase("EXIT")) {
//     // break;
//     // }

//     // // 1. Wysyłanie do serwera
//     // out.println(lineToSend);

//     // // 2. Czekanie na odpowiedź z serwera i jej wyświetlenie
//     // if (in.hasNextLine()) {
//     // String response = in.nextLine();
//     // // Zastąpienie IO.println()
//     // System.out.println("SERWER > " + response);
//     // } else {
//     // System.out.println("Serwer zamknął połączenie.");
//     // break;
//     // }
//     // }

//     // } catch (IOException e) {
//     // System.err.println("Wystąpił błąd komunikacji: " + e.getMessage());
//     // }
//     // System.out.println("Klient zakończył działanie.");
//     // }
// }

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Łączenie z serwerem " + SERVER_ADDRESS + ":" + PORT + "...");

        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner in = new Scanner(socket.getInputStream());
                BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Połączono. Wpisz wiadomości (wpisz 'quit' by zakończyć):");

            String lineToSend;

            while ((lineToSend = consoleIn.readLine()) != null) {

                // 1. Wysyłanie do serwera
                out.println(lineToSend);

                if (lineToSend.equalsIgnoreCase("quit")) {
                    break;
                }

                // 2. Oczekiwanie na odpowiedź
                if (in.hasNextLine()) {
                    String response = in.nextLine();
                    System.out.println("[Serwer] " + response);
                } else {
                    System.out.println("Serwer zamknął połączenie.");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Błąd połączenia/komunikacji: " + e.getMessage());
        }
        System.out.println("Klient zakończył działanie.");
    }
}