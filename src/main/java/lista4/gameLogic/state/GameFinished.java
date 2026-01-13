package lista4.gameLogic.state;

import lista4.gameLogic.GameContext;

public class GameFinished implements GameStateBehaviour{
    @Override
    public GameState getState() {
        return GameState.GAME_FINISHED;
    }

    @Override
    public void startGame(GameContext context) {

    }

    @Override
    public void finishGame(GameContext context) {

    }

    @Override
    public void startNegotiations(GameContext context) {

    }

    @Override
    public void resumeGame(GameContext context) {

    }
}
