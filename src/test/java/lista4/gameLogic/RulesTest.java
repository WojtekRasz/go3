package lista4.gameLogic;

import lista4.gameLogic.gameExceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RulesTest {

    private Board board;

    @BeforeEach
    void setup() {
        board = new Board();
    }

    // --------------------------------------------------
    // 1️⃣ Nie można zagrać na zajętym polu
    // --------------------------------------------------
    @Test
    void cannotPlaceStoneOnOccupiedField() throws Exception {
        Stone black = new Stone(0, 0, PlayerColor.BLACK, board);
        board.putStone(0, 0, black);

        Stone white = new Stone(0, 0, PlayerColor.WHITE, board);

        assertThrows(
                FieldOcupiedException.class,
                () -> board.putStone(0, 0, white)
        );
    }

    @Test
    void Capture() throws Exception {
        board.putStone(0, 1, new Stone(0, 1, PlayerColor.BLACK, board));
        board.putStone(1, 0, new Stone(1, 0, PlayerColor.BLACK, board));
        board.putStone(2, 1, new Stone(2, 1, PlayerColor.BLACK, board));
        board.putStone(1, 1, new Stone(1, 1, PlayerColor.WHITE, board));


        board.putStone(1, 2, new Stone(1, 2, PlayerColor.BLACK, board));

        assertEquals(board.getStone(1, 1), null);
    }

    @Test
    void suicideMoveIsForbidden() throws Exception {
        /*
            Układ:
              B
            B . B
              B

            White próbuje zagrać w środek → samobójstwo
         */

        board.putStone(0, 1, new Stone(0, 1, PlayerColor.BLACK, board));
        board.putStone(1, 0, new Stone(1, 0, PlayerColor.BLACK, board));
        board.putStone(2, 1, new Stone(2, 1, PlayerColor.BLACK, board));
        board.putStone(1, 2, new Stone(1, 2, PlayerColor.BLACK, board));

        Stone suicide = new Stone(1, 1, PlayerColor.WHITE, board);

        assertThrows(
                SuicideException.class,
                () -> board.putStone(1, 1, suicide)
        );

        // Pole musi pozostać puste
        assertTrue(board.isEmpty(1, 1));
    }


    @Test
    void koRulePreventsImmediateRecapture() throws Exception {
        /*
            Klasyczny układ Ko:
                W
              W B W
              B . B
              . B .

            Czarny zbija białego → Ko
            Biały NIE MOŻE od razu odbić
         */

        // Czarny kamienie
        board.putStone(1, 0, new Stone(1, 0, PlayerColor.WHITE, board));
        board.putStone(0, 1, new Stone(0, 1, PlayerColor.WHITE, board));
        board.putStone(2, 1, new Stone(2, 1, PlayerColor.WHITE, board));

        board.putStone(1, 1, new Stone(1, 1, PlayerColor.BLACK, board));
        board.putStone(0, 2, new Stone(0, 2, PlayerColor.BLACK, board));
        board.putStone(2, 2, new Stone(2, 2, PlayerColor.BLACK, board));
        board.putStone(1, 3, new Stone(1, 3, PlayerColor.BLACK, board));


        Stone whiteCapture = new Stone(1, 2, PlayerColor.WHITE, board);
        board.putStone(1, 2, whiteCapture);

        // Biały próbuje natychmiast odbić (Ko)
        Stone illegalRecapture = new Stone(1, 1, PlayerColor.BLACK, board);

        assertThrows(
                CaptureInKoException.class,
                () -> board.putStone(1, 1, illegalRecapture)
        );
    }
}
