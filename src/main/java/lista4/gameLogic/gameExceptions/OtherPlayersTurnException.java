package lista4.gameLogic.gameExceptions;

import lista4.gameLogic.GameManager;

public class OtherPlayersTurnException extends RuntimeException {
    private final GameManager.PlayerColor playerColor; //Kolor gracza, który teraz powinein rozegrać swoją turę
    public OtherPlayersTurnException(GameManager.PlayerColor playerColor) {
        this.playerColor = playerColor;
    }
    public GameManager.PlayerColor getPlayerColor(){ return playerColor; }
}
