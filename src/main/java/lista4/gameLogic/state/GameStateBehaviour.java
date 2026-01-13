package lista4.gameLogic.state;

import lista4.gameLogic.GameContext;

public interface GameStateBehaviour {
    GameState getState();

    void startGame(GameContext context);

    void finishGame(GameContext context);

    void startNegotiations(GameContext context);

    void resumeGame(GameContext context);
}
