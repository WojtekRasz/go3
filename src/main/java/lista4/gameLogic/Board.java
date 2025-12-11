package lista4.gameLogic;

public class Board {
    private int boardSize = 19;
    private Stone[][] board;

    public Board() {
        board = new Stone[boardSize][boardSize];
    }

    public boolean isFieldAvailable(int x, int y, Game.Player player){
        if(x >= 0 && y >= 0 && x < boardSize && y < boardSize){
            return (board[x][y] == null);
        }
        return false;
    }

}
