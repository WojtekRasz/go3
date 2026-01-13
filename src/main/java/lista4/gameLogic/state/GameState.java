package lista4.gameLogic.state;

public enum GameState {
    GAME_NOT_INITIALIZED(new GameNotInitialized()),
    GAME_RUNNING(new GameRunning()),
    GAME_PAUSED(new GamePaused()),
    GAME_FINISHED(new GameFinished()),;

    private final GameStateBehaviour gameStateBehaviour;

    public GameStateBehaviour getStateBehaviour() {
        return gameStateBehaviour;
    }

    GameState(GameStateBehaviour stateBehaviour) {
        gameStateBehaviour = stateBehaviour;
    }

}
