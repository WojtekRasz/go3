package lista4.gameLogic.state;

import lista4.gameLogic.GameContext;

public class GameNotInitialized implements GameStateBehaviour {

    @Override
    public GameState getState() {
        return GameState.GAME_NOT_INITIALIZED;
    }

    @Override
    public void startGame(GameContext context) {
        context.setGameState(GameState.GAME_RUNNING);
    }

    @Override
    public void finishGame(GameContext context) {

    }

    @Override
    public void pauseGame(GameContext context) {

    }

    @Override
    public void resumeGame(GameContext context) {

    }
}
