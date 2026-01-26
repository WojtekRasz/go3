package lista4.gameLogic;

/**
 * Represents the color of a player in the game.
 * <p>
 * The enum defines three values:
 * <ul>
 * <li>{@link #BLACK} – black player</li>
 * <li>{@link #WHITE} – white player</li>
 * <li>{@link #BOTH} – used when a message or action concerns both players</li>
 * </ul>
 */
public enum PlayerColor {
    /** Black player */
    BLACK,

    /** White player */
    WHITE,

    /** Represents both players */
    BOTH;

    /**
     * Returns the opposite player color.
     * <p>
     * For BLACK returns WHITE, for WHITE returns BLACK.
     * BOTH returns BOTH itself.
     *
     * @return Opposite {@link PlayerColor} or {@link #BOTH} if current is BOTH
     */
    public PlayerColor other() {
        if (this.equals(WHITE)) {
            return BLACK;
        }
        if (this.equals(BLACK)) {
            return WHITE;
        }
        return BOTH;
    }

    @Override
    public String toString() {
        if (this.equals(BLACK)) {
            return "BLACK";
        }
        if (this.equals(WHITE)) {
            return "WHITE";
        }
        return "BOTH";
    }
}
