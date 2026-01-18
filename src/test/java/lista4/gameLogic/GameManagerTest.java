package lista4.gameLogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lista4.gameInterface.GameOutputAdapter;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class GameManagerTest {

    private GameManager manager;
    private GameOutputAdapter adapter;

    @BeforeEach
    void setup() {
        manager = GameManager.getInstance();
        adapter = mock(GameOutputAdapter.class);
        manager.setAdapter(adapter);
        manager.startGame();
    }

    @Test
    void testGiveUpGame() {
        manager.giveUpGame(PlayerColor.BLACK);
        verify(adapter).sendWiningMassage(eq(PlayerColor.WHITE), eq(0), eq(0), eq(true));
    }
}
