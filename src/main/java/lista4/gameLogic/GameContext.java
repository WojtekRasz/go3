package lista4.gameLogic;

import lista4.gameLogic.state.GameState;

public class GameContext {

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

    public void endGame() {
        curGameState.getStateBehaviour().stopGame(this);
    }

    public void nextPlayer() {
        curGameState.getStateBehaviour().nextPlayer(this);
    }

}
