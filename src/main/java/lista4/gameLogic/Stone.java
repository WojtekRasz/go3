package lista4.gameLogic;

import java.util.HashSet;
import java.util.Set;

public class Stone {
    public static class Breath{
        int x;
        int y;

        public Breath(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof Breath){
                return ((Breath) o).x == this.x && ((Breath) o).y == this.y;
            }
            return false;
        }
    }

    private final int posX;
    private final int posY;
    private final GameManager.PlayerColor playerColor;
    private final Set<Breath> breathes;


    public Stone(int posX, int posY, GameManager.PlayerColor playerColor) {
        this.posX = posX;
        this.posY = posY;
        this.playerColor = playerColor;
        breathes = new HashSet<Breath>();
    }

    public GameManager.PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void removeBreath(Breath breath) {
        breathes.remove(breath);
    }

    public void addBreath(Breath breath) {
        breathes.add(breath);
    }

}
