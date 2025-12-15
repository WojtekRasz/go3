package lista4.gameLogic;

public class Field {
    private Stone stone;
    private Board board;
    private int x;
    private int y;

    Field(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Stone getStone() {
        return stone;
    }

    public  void putStone(Stone stone) {
        this.stone = stone;
    }
}
