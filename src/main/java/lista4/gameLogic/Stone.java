package lista4.gameLogic;

import lista4.gameLogic.gameExceptions.IllegalStoneOfBothColorsException;

import java.util.HashSet;
import java.util.Set;

public class Stone {

    private final int x;
    private final int y;
    private final PlayerColor playerColor;
    private final Board board;
    private final Field field;
    private final Set<Field> breaths;
    private StoneChain chain;

    public Stone(int x, int y, PlayerColor playerColor, Board board) throws IllegalStoneOfBothColorsException {
        this.x = x;
        this.y = y;

        if (playerColor == PlayerColor.BOTH) {
            throw new IllegalStoneOfBothColorsException();
        }

        this.playerColor = playerColor;
        this.board = board;
        this.field = board.getField(x, y);
        breaths = new HashSet<Field>();
        updateBreaths();
    }



    public void setChain(StoneChain chain) { this.chain = chain; }
    public StoneChain getChain() { return chain; }

    public Set<Field> getBreaths() {
        updateBreaths();
        return breaths;
    }

    // update breaths po ruchu
    public void updateBreaths() {
        breaths.clear();
        for (Field neighbour : field.getNeighbours()) {
            if (neighbour.getStone() == null) breaths.add(neighbour);
        }
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Board getBoard() { return board; }
}
