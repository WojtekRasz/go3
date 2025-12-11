package lista4.gameLogic;

public class Game {

    public enum Player {
        BLACK,
        WHITE
    }

    public static class Move{
        int x;
        int y;
        Player player;
    }

    private static Game instance;
    private Board board;



    public Game() {
        instance = this;
    }

    public static Game getInstance() {
        return instance;
    }

    public boolean validateMove(Move move){
        return board.isFieldAvailable(move.x, move.y, move.player);
    }


}
