package lista4.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The graphical client application for the Go game, built with JavaFX.
 * <p>
 * This class provides a visual interface for the game, featuring:
 * <ul>
 * <li>A {@link Canvas} based board for rendering the grid and stones.</li>
 * <li>Interactive mouse controls for placing stones.</li>
 * <li>Real-time updates handled by a background thread communicating with the
 * server.</li>
 * </ul>
 * </p>
 * <p>
 * Thread Safety: Since JavaFX is single-threaded for UI updates, this class
 * uses
 * {@link Platform#runLater(Runnable)} to process server messages that affect
 * the GUI.
 * </p>
 */
public class GUIClient extends Application {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int PORT = 12345;

    private Socket socket;
    private PrintWriter out;

    // UI Components
    private TextArea logArea;
    private Label playerInfoLabel;
    private Label blackCapturedLabel;
    private Label whiteCapturedLabel;
    private BorderPane topBar;
    private Canvas boardCanvas;

    private Button passButton;
    private Button resignButton;
    private Button acceptButton; // Przycisk do akceptacji terytorium
    private Button resumeButton; // Przycisk do wznowienia gry (jeśli nie zgadzamy się)

    private final int BOARD_SIZE = 19;
    private final int CELL_SIZE = 30;

    private boolean isNegotiationMode = false;
    private String myPlayerColor = "UNKNOWN";

    private List<String> markedBlackFields = new ArrayList<>();
    private List<String> markedWhiteFields = new ArrayList<>();
    private Color[][] boardState = new Color[BOARD_SIZE][BOARD_SIZE];

    /**
     * The main entry point for the JavaFX application.
     * Initializes the UI components and starts the server connection process.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        initComponents();
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(boardCanvas);
        root.setBottom(logArea);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Go Client - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();

        // --- Connect to server in background ---
        connectToServer(primaryStage);
    }

    /**
     * Initializes all graphical components (Buttons, Labels, Canvas) and event
     * listeners.
     */
    public void initComponents() {
        playerInfoLabel = new Label("Czekanie na gracza...");
        playerInfoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Score / Captures info
        Circle blackIcon = new Circle(8, Color.BLACK);
        blackCapturedLabel = new Label("0");
        Circle whiteIcon = new Circle(8, Color.WHITE);
        whiteIcon.setStroke(Color.BLACK);
        whiteCapturedLabel = new Label("0");

        HBox scoreBox = new HBox(10, playerInfoLabel, blackIcon, blackCapturedLabel, whiteIcon, whiteCapturedLabel);
        scoreBox.setAlignment(Pos.CENTER_LEFT);
        scoreBox.setPadding(new Insets(10));

        // Control Buttons
        passButton = new Button("Pomiń ruch");
        passButton.setOnAction(e -> sendCommand("SKIP"));

        resignButton = new Button("Poddaj się");
        resignButton.setOnAction(e -> sendCommand("GIVE UP"));

        acceptButton = new Button("Zatwierdź terytorium");
        // acceptButton.setStyle("-fx-background-color: lightgreen;"); // Opcjonalny
        // styl
        acceptButton.setVisible(false); // Domyślnie ukryty
        acceptButton.setManaged(false); // Domyślnie nie zajmuje miejsca
        // UWAGA: Sprawdź jaką komendę serwer oczekuje na akceptację (np. "ACCEPT",
        // "DONE", "AGREE")
        acceptButton.setOnAction(e -> sendCommand("ACCEPT"));

        resumeButton = new Button("Wznów grę");
        // resumeButton.setStyle("-fx-background-color: lightcoral;");
        resumeButton.setVisible(false);
        resumeButton.setManaged(false);
        // UWAGA: Sprawdź komendę na odrzucenie/wznowienie (np. "RESUME", "PLAY")
        resumeButton.setOnAction(e -> sendCommand("RESUME"));
        HBox buttonBox = new HBox(10, passButton, resignButton, acceptButton, resumeButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10));

        topBar = new BorderPane();
        topBar.setLeft(scoreBox);
        topBar.setRight(buttonBox);
        topBar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");

        // Log Area
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(10);
        logArea.setStyle("-fx-font-size: 13px;");

        // Game Board
        boardCanvas = new Canvas(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE);
        drawGrid();

        // Mouse Click Handler: Converts pixels to Grid Coordinates
        boardCanvas.setOnMouseClicked(event -> {
            int x = (int) (event.getX() / CELL_SIZE);
            int y = (int) (event.getY() / CELL_SIZE);
            if (x >= BOARD_SIZE || y >= BOARD_SIZE)
                return;

            if (isNegotiationMode) {
                char column = (char) ('a' + x);
                int row = y + 1;
                String fieldId = x + "," + y;
                if (!markedBlackFields.contains(fieldId) && !markedWhiteFields.contains(fieldId)) {
                    String command = "PROP + " + column + " " + row;
                    sendCommand(command);
                } else {
                    String command = "PROP - " + column + " " + row;
                    sendCommand(command);
                }

            } else {
                char column = (char) ('a' + x);
                String command = column + " " + (y + 1);
                sendCommand(command);
            }
        });
    }

    /**
     * Przełącza widoczność przycisków w zależności od trybu gry.
     */
    private void setNegotiationModeUI(boolean enable) {
        // Jeśli tryb negocjacji: ukryj Pass/Resign, pokaż Accept/Resume
        passButton.setVisible(!enable);
        passButton.setManaged(!enable);

        resignButton.setVisible(!enable);
        resignButton.setManaged(!enable);

        acceptButton.setVisible(enable);
        acceptButton.setManaged(enable);

        resumeButton.setVisible(enable);
        resumeButton.setManaged(enable);
    }

    /**
     * Rysuje znacznik (np. zielony) na polu.
     */
    public void drawMarker(int x, int y, Color color) {
        if (boardCanvas == null)
            return;
        GraphicsContext gc = boardCanvas.getGraphicsContext2D();

        double size = CELL_SIZE * 0.5; // Rozmiar znacznika (kwadrat lub kółko)
        double centerX = CELL_SIZE / 2.0 + x * CELL_SIZE;
        double centerY = CELL_SIZE / 2.0 + y * CELL_SIZE;

        gc.setFill(new Color(0, 1, 0, 0.6)); // Zielony z przezroczystością
        gc.fillRect(centerX - size / 2, centerY - size / 2, size, size);

        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeRect(centerX - size / 2, centerY - size / 2, size, size);
        String fieldId = x + "," + y;
        if (color == Color.BLACK) {
            markedBlackFields.add(fieldId); // Dodaj do lokalnej pamięci
        } else {
            markedWhiteFields.add(fieldId); // Dodaj do lokalnej pamięci
        }
    }

    /**
     * Przerysowuje pojedyncze pole, aby usunąć zielone zaznaczenie.
     */
    private void refreshCell(int x, int y) {
        GraphicsContext gc = boardCanvas.getGraphicsContext2D();

        // background
        double startX = x * CELL_SIZE;
        double startY = y * CELL_SIZE;
        gc.setFill(Color.web("#DEB887"));
        gc.fillRect(startX, startY, CELL_SIZE, CELL_SIZE);

        // lines
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(startX, startY + CELL_SIZE / 2.0, startX + CELL_SIZE, startY + CELL_SIZE / 2.0);
        gc.strokeLine(startX + CELL_SIZE / 2.0, startY, startX + CELL_SIZE / 2.0, startY + CELL_SIZE);
        // draw stone if exists
        if (boardState[x][y] != null) {
            drawStone(x, y, boardState[x][y]);
        }

        String fieldId = x + "," + y;
        if (markedBlackFields.contains(fieldId)) {
            markedBlackFields.remove(fieldId); // usun z lokalnej pamięci
        } else {
            markedWhiteFields.remove(fieldId); // usun z lokalnej pamięci
        }
    }

    /**
     * Establishes a socket connection to the server in a separate thread.
     * <p>
     * Sends the "GUI" handshake immediately upon connection.
     * </p>
     *
     * @param stage The main application stage, used for updating the title.
     */
    private void connectToServer(Stage stage) {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_ADDRESS, PORT);
                out = new PrintWriter(socket.getOutputStream(), true);

                Thread listener = new Thread(new ServerListener(socket, stage));
                listener.setDaemon(true); // Ensures thread dies when app closes
                listener.start();

                out.println("GUI"); // Handshake: Request GUI protocol
                out.println("GETBOARD"); // Request initial state

            } catch (IOException e) {
                Platform.runLater(() -> logArea.setText("Błąd połączenia: " + e.getMessage() + "\n"));
            }
        }).start();
    }

    /**
     * Sends a raw command string to the server.
     *
     * @param cmd The command to send (e.g., "a 10", "pass").
     */
    private void sendCommand(String cmd) {
        if (out != null) {
            out.println(cmd);
            System.out.println(cmd);
            logArea.setText("Wysłano: " + cmd + "\n");
        }
    }

    /**
     * Draws the background grid of the Go board using {@link GraphicsContext}.
     */
    private void drawGrid() {
        GraphicsContext gc = boardCanvas.getGraphicsContext2D();
        gc.setFill(Color.web("#DEB887")); // BurlyWood color
        gc.fillRect(0, 0, boardCanvas.getWidth(), boardCanvas.getHeight());

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        for (int i = 0; i < BOARD_SIZE; i++) {
            // Draw horizontal and vertical lines
            gc.strokeLine(CELL_SIZE / 2.0, CELL_SIZE / 2.0 + i * CELL_SIZE, (BOARD_SIZE - 0.5) * CELL_SIZE,
                    CELL_SIZE / 2.0 + i * CELL_SIZE);
            gc.strokeLine(CELL_SIZE / 2.0 + i * CELL_SIZE, CELL_SIZE / 2.0, CELL_SIZE / 2.0 + i * CELL_SIZE,
                    (BOARD_SIZE - 0.5) * CELL_SIZE);
        }
    }

    /**
     * Renders a stone at specific grid coordinates.
     *
     * @param x     The X grid coordinate (0-18).
     * @param y     The Y grid coordinate (0-18).
     * @param color The color of the stone (Black or White).
     */
    public void drawStone(int x, int y, Color color) {
        if (boardCanvas == null)
            return;
        GraphicsContext gc = boardCanvas.getGraphicsContext2D();

        double radius = CELL_SIZE * 0.4;
        double centerX = CELL_SIZE / 2.0 + x * CELL_SIZE;
        double centerY = CELL_SIZE / 2.0 + y * CELL_SIZE;

        if (color != Color.TRANSPARENT) {
            gc.setFill(color);
            gc.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            // Optional: stroke for better visibility
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(0.5);
            gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        } else {
            refreshCell(x, y);
        }
        if (color == Color.TRANSPARENT) {
            boardState[x][y] = null;
        } else {
            boardState[x][y] = color;
        }
    }

    /**
     * A background task that listens for messages from the server.
     * <p>
     * It is responsible for parsing protocol commands (e.g., "UPDATE BLACK 10 10")
     * and scheduling UI updates on the JavaFX Application Thread.
     * </p>
     */
    private class ServerListener implements Runnable {
        private final Socket socket;
        private final Stage stage;

        public ServerListener(Socket socket, Stage stage) {
            this.socket = socket;
            this.stage = stage;
        }

        @Override
        public void run() {
            try (Scanner in = new Scanner(socket.getInputStream())) {
                String name;
                // Read assigned player color/name
                if (in.hasNextLine()) {
                    name = in.nextLine();
                    myPlayerColor = name.contains("BLACK") ? "BLACK" : "WHITE";
                    Platform.runLater(() -> {
                        playerInfoLabel.setText("Grasz jako: " + name);
                        stage.setTitle("Go Client - " + name);
                    });
                }

                // Main Loop
                while (in.hasNextLine()) {
                    String message = in.nextLine();

                    // All UI updates must happen inside Platform.runLater
                    Platform.runLater(() -> {

                        // Protocol Parser
                        // 1. Wykrycie zmiany stanu gry na GAME_NOT_RUNNING (Tryb zaznaczania)
                        if (message.contains("NEGOTIATIONS")) {
                            isNegotiationMode = true;
                            logArea.appendText("SYSTEM: negocjacji.\n");
                            setNegotiationModeUI(true);
                        } else if (message.equals("RESUME_GAME") || message.equals("PLAY")) {
                            isNegotiationMode = false;
                            setNegotiationModeUI(false); // Przywróć stare przyciski
                            logArea.appendText("SYSTEM: Wznowiono grę.\n");
                        } else if (message.equals("CLEAR_BOARD")) {
                            drawGrid();
                            markedBlackFields.clear();
                            markedWhiteFields.clear();
                        } else if (message.startsWith("UPDATE")) {
                            try {
                                String[] parts = message.split(" ");
                                String colorStr = parts[1];
                                int x = Integer.parseInt(parts[2]);
                                int y = Integer.parseInt(parts[3]);

                                Color stoneColor = colorStr.equalsIgnoreCase("BLACK") ? Color.BLACK : Color.WHITE;
                                if (colorStr.equals("BLANK")) {
                                    stoneColor = Color.TRANSPARENT;
                                }
                                drawStone(x, y, stoneColor);
                                // drawGrid();
                            } catch (Exception e) {
                                logArea.setText("Błąd rysowania: " + e.getMessage() + "\n");
                            }
                        } else if (message.startsWith("REC_PROP")) {
                            try {
                                System.out.println(message);
                                String[] parts = message.split(" ");
                                String colorStr = parts[1];
                                int x = Integer.parseInt(parts[2]);
                                int y = Integer.parseInt(parts[3]);
                                String update_type = parts[4];

                                Color stoneColor = colorStr.equalsIgnoreCase("BLACK") ? Color.BLACK : Color.WHITE;
                                if (colorStr.equals("BLANK")) {
                                    stoneColor = Color.TRANSPARENT;
                                }
                                if (update_type.equals("+")) {// dodaj propozycję
                                    drawMarker(x, y, stoneColor);
                                } else { // usuń propozycje
                                    refreshCell(x, y);
                                }
                            } catch (Exception e) {
                                logArea.setText("Błąd rysowania: " + e.getMessage() + "\n");
                            }
                        } else {
                            logArea.setText("" + message + "\n");
                        }
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> logArea.setText("Utracono połączenie.\n"));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}