package lista4.dbModel;

import jakarta.persistence.*;

@Entity
public class MoveEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id") // Tak będzie się nazywać kolumna w bazie z ID gry
    private GameEntity game;

    private int moveNumber; // Który to ruch z kolei (0, 1, 2...)
    private int x;          // Współrzędna X na planszy
    private int y;          // Współrzędna Y na planszy
    private String color;   // "BLACK" lub "WHITE"
    private boolean isPass; // Czy to był ruch typu "pass"?

    public MoveEntity() {}

    public Long getId() { return id; }

    public GameEntity getGame() { return game; }
    public void setGame(GameEntity game) { this.game = game; }

    public int getMoveNumber() { return moveNumber; }
    public void setMoveNumber(int moveNumber) { this.moveNumber = moveNumber; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public boolean isPass() { return isPass; }
    public void setPass(boolean pass) { isPass = pass; }
}
