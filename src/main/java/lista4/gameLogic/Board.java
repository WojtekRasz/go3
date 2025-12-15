package lista4.gameLogic;

import lista4.gameLogic.gameExceptions.FieldNotAvailableException;

public class Board {
    private int boardSize = 19;
    private final Stone[][] board;

    public Board() {
        board = new Stone[boardSize][boardSize];
    }

    public boolean isFieldAvailable(int x, int y, GameManager.PlayerColor playerColor){
        if(x >= 0 && y >= 0 && x < boardSize && y < boardSize){
            return (board[x][y] == null);
        }
        return false;
    }

    public void putStone(int x, int y, Stone stone) throws FieldNotAvailableException {
        if(!isFieldAvailable(x, y, stone.getPlayerColor())){
            throw new FieldNotAvailableException(new GameManager.Move(x, y, stone.getPlayerColor()));
        }

        board[x][y] = stone;
    }


    public Stone getStone(int x, int y){
        if(x >= 0 && y >= 0 && x < boardSize && y < boardSize){
            return null;
        }
        return board[x][y];
    }
}
