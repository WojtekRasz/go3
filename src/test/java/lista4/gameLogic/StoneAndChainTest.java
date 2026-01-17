package lista4.gameLogic;

import lista4.gameLogic.gameExceptions.IllegalStoneOfBothColorsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoneAndChainTest {

    private Board board;
    private Stone black;

    @BeforeEach
    void setup() throws IllegalStoneOfBothColorsException {
        board = new Board();
        black = new Stone(0,0, PlayerColor.BLACK, board);
    }

    @Test
    void testBreathCountCorner() {
        assertEquals(2, black.getBreathCount()); // rogi planszy mają 2 wolne pola
    }

    @Test
    void testBreathCountEdge() {
        Stone blackEdge = new Stone(0,4, PlayerColor.BLACK, board);
        assertEquals(3, blackEdge.getBreathCount());
    }

    @Test
    void testChainMerge() throws IllegalStoneOfBothColorsException {
        Stone anotherBlack = new Stone(0,1, PlayerColor.BLACK, board);
        StoneChain chain1 = black.getChain();
        StoneChain chain2 = anotherBlack.getChain();
        chain1.merge(chain2);
        assertEquals(2, chain1.getStones().size());
    }

    @Test
    void testCaptureChain() throws IllegalStoneOfBothColorsException {
        StoneChain chain = black.getChain();
        chain.captureChain();
        assertTrue(board.isEmpty(0,0));
    }
}
