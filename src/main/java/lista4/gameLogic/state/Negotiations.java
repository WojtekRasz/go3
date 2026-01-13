package lista4.gameLogic.state;

import lista4.gameLogic.GameContext;

public class Negotiations implements GameStateBehaviour{
    @Override
    public GameState getState() {
        return GameState.NEGOTIATIONS;
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

    }

    @Override
    public void resumeGame(GameContext context) {
        context.setGameState(GameState.GAME_RUNNING);
    }
}

