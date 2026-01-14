package lista4.gameLogic;

import lista4.gameLogic.gameExceptions.IllegalStoneOfBothColorsException;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single stone on the Go board.
 * <p>
 * Each stone has a color, belongs to a board, and keeps track of its "breaths" (empty adjacent fields).
 * Stones can also belong to a {@link StoneChain} for capturing logic.
 */
public class Stone {

    /** X coordinate on the board */
    private final int x;

    /** Y coordinate on the board */
    private final int y;

    /** Color of the stone (BLACK or WHITE) */
    private final PlayerColor playerColor;

    /** Reference to the board this stone belongs to */
    private final Board board;

    /** Reference to the field this stone occupies */
    private final Field field;

    /** Set of empty adjacent fields ("breaths") */
    private final Set<Field> breaths;

    /** The chain this stone belongs to */
    private StoneChain chain;

    /**
     * Creates a new stone at the specified position and color.
     *
     * @param x X-coordinate on the board
     * @param y Y-coordinate on the board
     * @param playerColor Color of the stone (cannot be BOTH)
     * @param board The board this stone belongs to
     * @throws IllegalStoneOfBothColorsException if the color is BOTH
     */
    public Stone(int x, int y, PlayerColor playerColor, Board board) throws IllegalStoneOfBothColorsException {
        this.x = x;
        this.y = y;

        if (playerColor == PlayerColor.BOTH) {
            throw new IllegalStoneOfBothColorsException();
        }

        this.playerColor = playerColor;
        this.board = board;
        this.field = board.getField(x, y);
        breaths = new HashSet<>();
        updateBreaths();
    }

    /**
     * Assigns this stone to a {@link StoneChain}.
     *
     * @param chain The chain to assign
     */
    public void setChain(StoneChain chain) { this.chain = chain; }

    /**
     * Returns the {@link StoneChain} this stone belongs to.
     *
     * @return The stone chain, or null if not assigned
     */
    public StoneChain getChain() { return chain; }

    /**
     * Returns the current set of empty adjacent fields ("breaths").
     * The set is updated before returning.
     *
     * @return Set of neighboring empty {@link Field}s
     */
    public Set<Field> getBreaths() {
        updateBreaths();
        return breaths;
    }

    /**
     * Updates the set of breaths by checking neighboring fields.
     * Should be called after moves that may affect neighboring stones.
     */
    public void updateBreaths() {
        breaths.clear();
        for (Field neighbour : field.getNeighbours()) {
            if (neighbour.getStone() == null) breaths.add(neighbour);
        }
    }

    /** Returns the color of the stone */
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    /** Returns the X coordinate of the stone */
    public int getX() { return x; }

    /** Returns the Y coordinate of the stone */
    public int getY() { return y; }

    /** Returns the board this stone belongs to */
    public Board getBoard() { return board; }

    public int getBreathCount(){
        updateBreaths();
        return breaths.size();
    }
}
