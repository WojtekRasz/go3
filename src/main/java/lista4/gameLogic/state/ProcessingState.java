package lista4.gameLogic.state;

import lista4.gameLogic.GameContext;

//TODO Potencjalnie niepotrzebne, sprawdzić, czy nie trza usunąć
public class ProcessingState implements GameStateBehaviour {

    @Override
    public GameState getState() {
        return GameState.GAME_PROCESSING;
    }

    //Na razie nie używamy tego stanu

    @Override
    public void startGame(GameContext context) {

    }

    @Override
    public void stopGame(GameContext context) {

    }

    @Override
    public void nextPlayer(GameContext context) {

    }


}
