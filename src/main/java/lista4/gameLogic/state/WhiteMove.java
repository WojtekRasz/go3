package lista4.gameLogic.state;

import lista4.gameLogic.GameContext;

public class WhiteMove implements GameStateBehaviour {

    @Override
    public GameState getState() {
        return GameState.WHITE_MOVE;
    }

    @Override
    public void startGame(GameContext context) {
        //Gra rozpoczęta, więc nic nie robi
    }

    @Override
    public void stopGame(GameContext context) {
        context.setGameState(GameState.GAME_NOT_RUNNING);
    }

    @Override
    public void nextPlayer(GameContext context) {
        context.setGameState(GameState.BLACK_MOVE);
    }

}
