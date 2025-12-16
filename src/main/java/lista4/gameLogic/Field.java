package lista4.gameLogic;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private Stone stone;
    private Board board;
    private int x;
    private int y;

    Field(int x, int y, Board board){
        this.board = board;
        this.x = x;
        this.y = y;
    }

    public Stone getStone() {
        return stone;
    }

    public  void putStone(Stone stone) {
        this.stone = stone;
    }

    public List<Field> getNeighbours(){
        ArrayList<Field> neighbours = new ArrayList<Field>();
        int i = 0;
        for(Board.Direction direction : Board.Direction.values()){
            Field neighbour = board.getField(x + direction.getX(), y + direction.getY());
            if(neighbour == null) continue;

            neighbours.add(neighbour);
        }
        return neighbours;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
