package lista4.gameLogic.state;

public enum GameState {
    GAME_NOT_RUNNING(new WaitingState()),
    BLACK_MOVE(new BlackMove()),
    WHITE_MOVE(new WhiteMove());

    private final GameStateBehaviour gameStateBehaviour;

    public GameStateBehaviour getStateBehaviour() {
        return gameStateBehaviour;
    }

    GameState(GameStateBehaviour stateBehaviour) {
        gameStateBehaviour = stateBehaviour;
    }

}
