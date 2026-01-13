package lista4.gameLogic;

public enum PlayerColor {
    BLACK,
    WHITE,
    BOTH;

    public PlayerColor other() {
        if (this.equals(WHITE)) {
            return BLACK;
        }
        if (this.equals(BLACK)) {
            return WHITE;
        }
        return BOTH;
    }
}
