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
    void testMakeMove() {
        // RUCH GRACZA
        GameManager.resetForTests();
        Move move = new Move(0, 0, PlayerColor.BLACK);
        manager.makeMove(move);

        // SPRAWDZENIA
        verify(adapter, times(2)).sendCurrentPlayer(any()); // start + move
        verify(adapter, atLeastOnce()).sendBoard(any(), eq(PlayerColor.BOTH));
    }

    @Test
    void testGiveUpGame() {
        manager.giveUpGame(PlayerColor.BLACK);
        verify(adapter).sendWiningMassage(eq(PlayerColor.WHITE), eq(0), eq(0), eq(true));
    }
}
