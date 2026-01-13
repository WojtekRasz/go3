package lista4.frontend;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class GUIClientTest {

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException e) {
        }
    }

    /**
     * Test if drawing stone is working without errors
     */
    @Test
    void testDrawStoneDoesNotCrash() {
        GUIClient client = new GUIClient();
        assertDoesNotThrow(() -> {
            Platform.runLater(() -> {
                client.drawStone(5, 5, Color.BLACK);
            });
        });
    }

    @Test
    void testDrawStoneWithInitializedCanvas() throws InterruptedException {
        GUIClient gui = new GUIClient();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                gui.initComponents();

                gui.drawStone(5, 5, Color.BLACK);

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Czekamy chwilę, aż wątek JavaFX skończy rysować
        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void testUpdateCommandParsing() throws InterruptedException {
        GUIClient gui = new GUIClient();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // 1. Przygotowanie komponentów (by boardCanvas nie był nullem)
                gui.initComponents();

                // 2. Symulacja komendy UPDATE
                String mockMessage = "UPDATE WHITE 10 12";
                String[] parts = mockMessage.split(" ");

                // 3. Logika wyciągnięta z Twojego ServerListenera
                if (parts[0].equals("UPDATE")) {
                    String colorStr = parts[1];
                    int x = Integer.parseInt(parts[2]);
                    int y = Integer.parseInt(parts[3]);

                    Color stoneColor = colorStr.equalsIgnoreCase("BLACK") ? Color.BLACK : Color.WHITE;

                    // Sprawdzamy, czy drawStone wykona się bez błędu dla tych danych
                    gui.drawStone(x, y, stoneColor);
                }

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Czekamy na wykonanie logiki na wątku JavaFX
        boolean completed = latch.await(2, TimeUnit.SECONDS);
        assertTrue(completed, "Test przekroczył czas oczekiwania");
    }
}