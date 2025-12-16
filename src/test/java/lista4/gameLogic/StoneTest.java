package lista4.gameLogic;

import lista4.gameLogic.gameExceptions.IllegalStoneOfBothColorsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoneTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void stoneInCenter_hasFourBreaths() throws Exception {
        Stone stone = new Stone(10, 10, GameManager.PlayerColor.BLACK, board);
        board.putStone(10, 10, stone);

        stone.updateBreaths();

        assertEquals(4, stone.getBreathCount(), "Kamień w środku planszy powinien mieć 4 oddechy");
    }

    @Test
    void stoneInCorner_hasTwoBreaths() throws Exception {
        Stone stone = new Stone(0, 0, GameManager.PlayerColor.BLACK, board);
        board.putStone(0, 0, stone);

        stone.updateBreaths();

        assertEquals(2, stone.getBreathCount(),
                "Kamień w rogu planszy powinien mieć 2 oddechy");
    }

    @Test
    void stoneOnEdge_hasThreeBreaths() throws Exception {
        Stone stone = new Stone(0, 10, GameManager.PlayerColor.BLACK, board);
        board.putStone(0, 10, stone);

        stone.updateBreaths();

        for (Field f : stone.getBreaths()) {
            System.out.println("(" + f.getX() + ", " + f.getY() + ")");
        }

        assertEquals(3, stone.getBreathCount(),
                "Kamień na krawędzi planszy powinien mieć 3 oddechy");
    }

    @Test
    void adjacentStone_removesBreath() throws Exception {
        Stone black = new Stone(5, 5, GameManager.PlayerColor.BLACK, board);
        board.putStone(5, 5, black);

        Stone white = new Stone(5, 6, GameManager.PlayerColor.WHITE, board);
        board.putStone(5, 6, white);

        black.updateBreaths();

        assertEquals(3, black.getBreathCount(), "Sąsiadujący kamień powinien zabrać jeden oddech");
    }

    @Test
    void updateBreaths_doesNotThrowOnBoardEdge() throws Exception {
        Stone stone = new Stone(18, 18, GameManager.PlayerColor.BLACK, board);
        board.putStone(18, 18, stone);

        assertDoesNotThrow(stone::updateBreaths,
                "updateBreaths nie powinno rzucać wyjątku na krawędzi planszy");
    }

    @Test
    void stoneCannotBeOfBothColors() {
        assertThrows(IllegalStoneOfBothColorsException.class, () -> new Stone(1, 1, GameManager.PlayerColor.BOTH, board),
                "Kamień nie może mieć koloru BOTH");
    }
}
