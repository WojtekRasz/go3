package lista4.gameLogic.state;

import lista4.gameLogic.GameContext;

public class WaitingState implements GameStateBehaviour {

    @Override
    public GameState getState() {
        return GameState.GAME_NOT_RUNNING;
    }

    @Override
    public void startGame(GameContext context) {
        context.setGameState(GameState.BLACK_MOVE);
    }

    @Override
    public void stopGame(GameContext context) {
        //Gra zatrzymana, nic nie robi
    }

    @Override
    public void nextPlayer(GameContext context) {
        //Gra zatrzymana, więc nie ma nowego gracza
    }


}
