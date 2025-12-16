package lista4.gameLogic;

import lista4.gameLogic.gameExceptions.FieldNotAvailableException;

public class Board {

    public enum Direction{
        UP(0, 1),
        RIGHT(1, 0),
        DOWN(0, -1),
        LEFT(-1, 0);

        private final int x;
        private final int y;

        Direction(int x, int y){
            this.x = x;
            this.y = y;
        }

        public int getX(){
            return x;
        }

        public int getY(){
            return y;
        }
    }

    private int boardSize = 19;
    private final Field[][] board;

    public Board() {
        board = new Field[boardSize][boardSize];
        for(int y = 0; y < boardSize; y++){
            for(int x = 0; x < boardSize; x++){
                board[x][y] = new Field(x, y, this);
            }
        }
    }

    public boolean inBoardBoundries(int x, int y){
        return x >= 0 && y >= 0 && x < boardSize && y < boardSize;
    }

    public boolean isEmpty(int x, int y){
        if(inBoardBoundries(x,y)){
            return (board[x][y].getStone() == null);
        }
        return false;
    }

    public boolean isFieldAvailable(int x, int y, GameManager.PlayerColor playerColor){
        return isEmpty(x,y);
    }

    public void putStone(int x, int y, Stone stone) throws FieldNotAvailableException {
        if(!isFieldAvailable(x, y, stone.getPlayerColor())){
            throw new FieldNotAvailableException(new GameManager.Move(x, y, stone.getPlayerColor()));
        }

        board[x][y].putStone(stone);
        for(Field neighbour: board[x][y].getNeighbours()){
            if(neighbour == null) continue;

            if(neighbour.getStone() != null){
                neighbour.getStone().removeBreath(board[x][y]);
            }
        }
    }

    public Field getField(int x, int y){
        if(!inBoardBoundries(x,y)) return null;
        return board[x][y];
    }

    public Stone getStone(int x, int y){
        if(inBoardBoundries(x, y)){
            return board[x][y].getStone();
        }
        return null;
    }
}
