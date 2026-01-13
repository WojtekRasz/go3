package lista4.gameLogic;

import lista4.gameLogic.state.GameState;

public class GameContext {

    PlayerColor curPlayerColor;
    int consecutivePasses;

    GameState curGameState;

    GameContext(GameState initialState) {
        curGameState = initialState;
    }

    public GameState getGameState() {
        return curGameState;
    }

    public void setGameState(GameState gameState) {
        curGameState = gameState;
    }

    public void startGame() {
        curGameState.getStateBehaviour().startGame(this);
    }

    public void stopGame() {
        curGameState.getStateBehaviour().finishGame(this);
    }

    public void nextPlayer() {

        curPlayerColor = curPlayerColor.other();
    }

    public PlayerColor getPlayerColor() {
        return curPlayerColor;
    }

    public void setCurPlayerColor(PlayerColor playerColor) {
        this.curPlayerColor = playerColor;
    }

    public void passNextPlayer() {
        nextPlayer();
        consecutivePasses++;
    }
    public int getConsecutivePasses() {
        return consecutivePasses;
    }
    public void resetPasses(){
        consecutivePasses = 0;
    }

}
