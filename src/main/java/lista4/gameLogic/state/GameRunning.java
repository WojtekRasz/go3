package lista4.gameLogic.state;

import lista4.gameLogic.GameContext;

public class GameRunning implements GameStateBehaviour{
    @Override
    public GameState getState() {
        return GameState.GAME_RUNNING;
    }

    @Override
    public void startGame(GameContext context) {

    }

    @Override
    public void finishGame(GameContext context) {
        context.setGameState(GameState.GAME_FINISHED);
    }

    @Override
    public void startNegotiations(GameContext context) {
        context.setGameState(GameState.NEGOTIATIONS);
    }

    @Override
    public void resumeGame(GameContext context) {

    }
}
