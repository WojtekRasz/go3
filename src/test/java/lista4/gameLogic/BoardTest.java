package lista4.gameLogic;

import lista4.gameLogic.gameExceptions.FieldNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void newBoard_hasNoStones() {
        for (int x = 0; x < 19; x++) {
            for (int y = 0; y < 19; y++) {
                assertNull(board.getStone(x, y), "Nowa plansza powinna być pusta");
            }
        }
    }

    @Test
    void putStone_placesStoneOnBoard() throws Exception {
        Stone stone = new Stone(3, 3, PlayerColor.BLACK, board);

        board.putStone(3, 3, stone);

        assertNotNull(board.getStone(3, 3));
        assertEquals(PlayerColor.BLACK, board.getStone(3, 3).getPlayerColor());
    }

    @Test
    void cannotPutStoneOnOccupiedField() throws Exception {
        board.putStone(1, 1,
                new Stone(1, 1, PlayerColor.BLACK, board));

        assertThrows(FieldNotAvailableException.class, () -> board.putStone(1, 1,
                new Stone(1, 1, PlayerColor.WHITE, board)));
    }

    @Test
    void getField_outsideBoard_returnsNull() {
        assertNull(board.getField(-1, 0));
        assertNull(board.getField(0, -1));
        assertNull(board.getField(19, 0));
        assertNull(board.getField(0, 19));
    }

    @Test
    void isEmpty_returnsTrueForEmptyField() {
        assertTrue(board.isEmpty(5, 5));
    }

    @Test
    void isEmpty_returnsFalseForOccupiedField() throws Exception {
        board.putStone(5, 5,
                new Stone(5, 5, PlayerColor.BLACK, board));

        assertFalse(board.isEmpty(5, 5));
    }

    @Test
    void isEmpty_outsideBoard_returnsFalse() {
        assertFalse(board.isEmpty(-1, 0));
        assertFalse(board.isEmpty(0, -1));
        assertFalse(board.isEmpty(19, 0));
        assertFalse(board.isEmpty(0, 19));
    }
}
