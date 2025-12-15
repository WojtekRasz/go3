package lista4.gameLogic;

import java.util.HashSet;
import java.util.Set;

public class Stone {

    private final int x;
    private final int y;
    private final GameManager.PlayerColor playerColor;
    private final Board board;
    private final Set<Field> breathes;


    public Stone(int x, int y, GameManager.PlayerColor playerColor, Board board) {
        this.x = x;
        this.y = y;
        this.playerColor = playerColor;
        this.board = board;
        breathes = new HashSet<Field>();
        updateBreaths();
    }

    public GameManager.PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void updateBreaths(){
        for (Board.Direction direction : Board.Direction.values()) {
            if(board.isEmpty(x + direction.getX(), y + direction.getY())){
                addBreath(board.getField(x + direction.getX(), y + direction.getY()));
            }
            else {
                removeBreath(board.getField(x + direction.getX(), y + direction.getY()));
            }
        }
    }

    public void removeBreath(Field breath) {
        breathes.remove(breath);
    }

    public void addBreath(Field breath) {
        breathes.add(breath);
    }

}
