package lista4.gameLogic.gameExceptions;

import lista4.gameLogic.PlayerColor;

public class OtherPlayersTurnException extends RuntimeException {
    private final PlayerColor playerColor; // Kolor gracza, który teraz powinein rozegrać swoją turę

    public OtherPlayersTurnException(PlayerColor playerColor) {
        super("nie twoja kolej na ruch. ");
        this.playerColor = playerColor;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }
}
