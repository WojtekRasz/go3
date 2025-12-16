package lista4.gameLogic.state;

import lista4.gameLogic.GameContext;

public interface GameStateBehaviour {
    GameState getState();
    void startGame(GameContext context);
    void stopGame(GameContext context);
    void nextPlayer(GameContext context);
    // GameStateBehaviour changePlayer();
}
