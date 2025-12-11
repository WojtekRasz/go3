package lista4;

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



    public Game() {
        instance = this;
    }

    public static Game getInstance() {
        return instance;
    }

    private boolean validateMove(Move move){
        return false;
    }


}
