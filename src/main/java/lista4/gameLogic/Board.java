package lista4.gameLogic;

import lista4.gameLogic.gameExceptions.CaptureInKoException;
import lista4.gameLogic.gameExceptions.FieldNotAvailableException;
import lista4.gameLogic.gameExceptions.FieldOcupiedException;
import lista4.gameLogic.gameExceptions.SuicideException;

import java.util.HashSet;
import java.util.Set;

public class Board {

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

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    private boolean ko;

    private final int boardSize = 19;
    private final Field[][] board;

    public Board() {
        board = new Field[boardSize][boardSize];
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                board[x][y] = new Field(x, y, this);
            }
        }
    }

    public boolean inBoardBoundries(int x, int y) {
        return x >= 0 && y >= 0 && x < boardSize && y < boardSize;
    }

    public boolean isEmpty(int x, int y) {
        if (inBoardBoundries(x, y)) {
            return (board[x][y].getStone() == null);
        }

        return false;
    }

    public boolean checkSuicide(Set<StoneChain> chains, Stone stone){
        int allBreaths = 0;
        allBreaths += stone.getBreaths().size();
        for(StoneChain stonesChain : chains) {
            allBreaths += stonesChain.getBreathCount();
        }
        return allBreaths == 0;
    }

    public void putStone(int x, int y, Stone stone) throws FieldOcupiedException {
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

        boolean suicide = checkSuicide(friendlyNeighbourChain, stone);

        if(stonesChainsToCapture.isEmpty() && suicide) {
            removeStone(x, y) ;
            throw new SuicideException(new Move(x, y, stone.getPlayerColor()));
        }
        else if(!stonesChainsToCapture.isEmpty() && ko){
            removeStone(x, y) ;
            throw new CaptureInKoException();
        }
        else {
            ko = suicide;

            stone.setChain(new StoneChain(stone));
            for(StoneChain stonesChain : friendlyNeighbourChain) {
                stone.getChain().merge(stonesChain);
            }

            for(StoneChain stonesChain : stonesChainsToCapture) {
                stone.getChain().captureChain();
            }

        }


    }

    public void removeStone(int x, int y){
        getField(x, y).putStone(null);
    }

    public Field getField(int x, int y) {
        if (!inBoardBoundries(x, y))
            return null;
        return board[x][y];
    }

    public Stone getStone(int x, int y) {
        if (inBoardBoundries(x, y)) {
            return board[x][y].getStone();
        }
        return null;
    }

    public int getSize() {
        return this.boardSize;
    }
}
