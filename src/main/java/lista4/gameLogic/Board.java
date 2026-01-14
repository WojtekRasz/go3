package lista4.gameLogic;

import lista4.gameLogic.gameExceptions.CaptureInKoException;
import lista4.gameLogic.gameExceptions.FieldNotAvailableException;
import lista4.gameLogic.gameExceptions.FieldOcupiedException;
import lista4.gameLogic.gameExceptions.SuicideException;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the Go board and handles stone placement, capturing, and rule enforcement.
 * Manages board state including Ko situations, suicides, and chains of stones.
 */
public class Board {
    /**
     * Represents the four orthogonal directions for neighboring fields.
     */
    public enum Direction {
        UP(0, 1),
        RIGHT(1, 0),
        DOWN(0, -1),
        LEFT(-1, 0);

        private final int x;
        private final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /** @return X offset for this direction */
        public int getX() {
            return x;
        }

        /** @return Y offset for this direction */
        public int getY() {
            return y;
        }
    }

    private boolean ko;
    private Stone koStone;

    private final int boardSize = 19;
    private final Field[][] board;


    /**
     * Initializes a new empty board with {@code boardSize} x {@code boardSize} fields.
     */
    public Board() {
        ko = false;
        board = new Field[boardSize][boardSize];
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                board[x][y] = new Field(x, y, this);
            }
        }
    }


    /**
     * Checks whether the specified coordinates are inside the board.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if coordinates are within board bounds
     */
    public boolean inBoardBoundries(int x, int y) {
        return x >= 0 && y >= 0 && x < boardSize && y < boardSize;
    }

    /**
     * Checks if the field at (x, y) is empty (no stone present).
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if the field is empty
     */
    public boolean isEmpty(int x, int y) {
        if (inBoardBoundries(x, y)) {
            return (board[x][y].getStone() == null);
        }

        return false;
    }

    /**
     * Determines if placing a stone would be suicide, taking into account 
     * friendly chains and their liberties.
     *
     * @param chains Set of neighboring friendly chains
     * @param stone Stone to be placed
     * @return true if the move would result in suicide
     */
    public boolean checkSuicide(Set<StoneChain> chains, Stone stone){
        int allBreaths = 0;
        allBreaths += stone.getChain().getBreathCount();
        for(StoneChain stonesChain : chains) {
            allBreaths += stonesChain.getBreathCount();
        }
        return allBreaths == 0;
    }

    /**
     * Places a stone on the board and handles capturing, merging chains, 
     * suicide checks, and Ko rule.
     *
     * @param x X coordinate to place the stone
     * @param y Y coordinate to place the stone
     * @param stone Stone to be placed
     * @throws FieldNotAvailableException if the field is already occupied or move is illegal
     */
    public void putStone(int x, int y, Stone stone) throws FieldNotAvailableException {
        Set<StoneChain> friendlyNeighbourChain = new HashSet<>();
        Set<StoneChain> stonesChainsToCapture = new HashSet<>();
        if (!isEmpty(x,y)) {
            throw new FieldOcupiedException(new Move(x, y, stone.getPlayerColor()));
        }

        board[x][y].putStone(stone);

        for(Field neighbour : board[x][y].getNeighbours()) {
            if (neighbour.getStone() == null) continue;

            Stone neighbourStone = neighbour.getStone();
            if(neighbourStone.getPlayerColor() == stone.getPlayerColor()) {
                friendlyNeighbourChain.add(neighbourStone.getChain());
            }
            else {
                if(neighbourStone.getChain().isDead()) {
                    stonesChainsToCapture.add(neighbourStone.getChain());
                }
            }
        }

        boolean hasCaptured = false;

        if(!stonesChainsToCapture.isEmpty() && ko){
            for(StoneChain stonesChain : stonesChainsToCapture) {
                if(stonesChain.getStones().contains(koStone)) {
                    removeStone(x, y) ;
                    throw new CaptureInKoException();
                }
            }
        }

        boolean suicide = checkSuicide(friendlyNeighbourChain, stone);

        if(!ko){

            for(StoneChain stonesChain : stonesChainsToCapture) {
                stonesChain.captureChain();
                System.out.println("BICIE");
                hasCaptured = true;
            }
        }

        if(!hasCaptured && suicide) {
            System.out.println("SAMOBÓJSTWO");
            removeStone(x, y) ;
            throw new SuicideException(new Move(x, y, stone.getPlayerColor()));
        }

        else {
            ko = suicide;
            if(ko) koStone = stone;
            else koStone = null;

            stone.setChain(new StoneChain(stone));
            for(StoneChain stonesChain : friendlyNeighbourChain) {
                stone.getChain().merge(stonesChain);
            }
        }
    }

    /**
     * Removes the stone from the specified coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void removeStone(int x, int y){
        getField(x, y).putStone(null);
    }

    /**
     * Returns the Field object at the given coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return Field at coordinates, or null if out of bounds
     */
    public Field getField(int x, int y) {
        if (!inBoardBoundries(x, y))
            return null;
        return board[x][y];
    }

    /**
     * Returns the Stone at the specified coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return Stone at coordinates, or null if empty or out of bounds
     */
    public Stone getStone(int x, int y) {
        if (inBoardBoundries(x, y)) {
            return board[x][y].getStone();
        }
        return null;
    }


    /**
     * Returns the size of the board (number of fields per side).
     *
     * @return board size
     */
    public int getSize() {
        return this.boardSize;
    }
}
