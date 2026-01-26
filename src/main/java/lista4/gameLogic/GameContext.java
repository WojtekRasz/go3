package lista4.gameLogic;

import lista4.dbModel.GameEntity;
import lista4.gameLogic.state.GameState;

/**
 * Represents the current context of a Go game.
 * 
 * Keeps track of the current player, game state, and consecutive passes.
 * Provides methods to control game flow (start, stop, next player, pass).
 */
import java.util.HashSet;
import java.util.Set;

public class GameContext {

    /** Current player's color */
    PlayerColor curPlayerColor;

    /** Number of consecutive passes made by both players */
    int consecutivePasses;

    Set<Integer> blackTerritory = new HashSet<>();
    Set<Integer> whiteTerritory = new HashSet<>();

    int whiteCaptured = 0;
    int blackCaptured = 0;

    GameState curGameState;

    GameEntity curGameEntity;
    int moveNumber = 0;

    /**
     * Creates a new GameContext with the specified initial state.
     *
     * @param initialState The initial state of the game
     */
    GameContext(GameState initialState) {
        curGameState = initialState;
    }

    /**
     * Returns the current game state.
     *
     * @return Current GameState
     */
    public GameState getGameState() {
        return curGameState;
    }

    /**
     * Sets the current game state.
     *
     * @param gameState New GameState to set
     */
    public void setGameState(GameState gameState) {
        curGameState = gameState;
    }

    /**
     * Starts the game by delegating to the current state's behaviour.
     */
    public void startGame() {
        curGameState.getStateBehaviour().startGame(this);
    }

    /**
     * Stops the game by delegating to the current state's behaviour.
     */
    public void stopGame() {
        curGameState.getStateBehaviour().finishGame(this);
    }

    /**
     * Switches to the next player.
     */
    public void finishGame() {
        curGameState.getStateBehaviour().finishGame(this);
    }

    public void startNegotiations() {
        curGameState.getStateBehaviour().startNegotiations(this);
    }

    public void resumeGame() {
        curGameState.getStateBehaviour().resumeGame(this);
    }

    public void nextPlayer() {
        curPlayerColor = curPlayerColor.other();
        moveNumber++;
    }

    /**
     * Returns the current player's color.
     *
     * @return PlayerColor of current player
     */
    public PlayerColor getCurPlayerColor() {
        return curPlayerColor;
    }

    /**
     * Sets the current player's color.
     *
     * @param playerColor PlayerColor to set as current
     */
    public void setCurPlayerColor(PlayerColor playerColor) {
        this.curPlayerColor = playerColor;
    }

    /**
     * Passes the turn to the next player and increments consecutive passes counter.
     */
    public void passNextPlayer() {
        nextPlayer();
        consecutivePasses++;
    }

    /**
     * Returns the number of consecutive passes made by both players.
     *
     * @return Number of consecutive passes
     */
    public int getConsecutivePasses() {
        return consecutivePasses;
    }

    /**
     * Resets the consecutive passes counter to zero.
     */
    public void resetPasses() {
        consecutivePasses = 0;
    }

    /**
     * Add territory
     * @param playerColor - territory to remove
     * @param x - x cord of territory
     * @param y - y cord of territory
     */
    public void addTerritory(PlayerColor playerColor, int x, int y) {
        int cordsCode = 100 * y + x; //Code of territory
        if (playerColor == PlayerColor.WHITE) {
            if (!blackTerritory.contains(cordsCode))
                whiteTerritory.add(cordsCode);
        }
        if (playerColor == PlayerColor.BLACK) {
            if (!whiteTerritory.contains(cordsCode))
                blackTerritory.add(cordsCode);
        }
    }

    /**
     * Remove territory
     * @param playerColor - territory to remove
     * @param x - x cord of territory
     * @param y - y cord of territory
     */
    public void removeTerritory(PlayerColor playerColor, int x, int y) {
        int cordsCode = 100 * y + x; // Code of territory
        if (playerColor == PlayerColor.WHITE) {
            whiteTerritory.remove(cordsCode);
        }
        if (playerColor == PlayerColor.BLACK) {
            blackTerritory.remove(cordsCode);
        }
    }

    /**
     * clears territories
     */
    public void clearTerritories() {
        blackTerritory.clear();
        whiteTerritory.clear();
    }

    /**
     * Returns black points (Black territory and white captured stones (So captured by black)
     * @return points of black player
     */
    public int blackPoints() {
        return blackTerritory.size() + whiteCaptured;
    }

    /**
     * Returns white points (White territory and black captured stones (So captured by white)
     * @return points of white player
     */
    public int whitePoints() {
        return whiteTerritory.size() + blackCaptured;
    }

    /**
     * Increment number of captured stones
     * @param playerColor color of stone to increment
     */
    public void addCaptured(PlayerColor playerColor) {
        if (playerColor == PlayerColor.WHITE) {
            whiteCaptured++;
        }
        if (playerColor == PlayerColor.BLACK) {
            blackCaptured++;
        }
    }

    /**
     * Returns captured stones of each colors
     * @param playerColor color of captured stones
     * @return captured stones of given color
     */
    public int getCaptured(PlayerColor playerColor) {
        if (playerColor == PlayerColor.WHITE) {
            return whiteCaptured;
        }
        if (playerColor == PlayerColor.BLACK) {
            return blackCaptured;
        }
        return 0;
    }

    public GameEntity getCurGameEntity() {
        return curGameEntity;
    }

    public void setCurGameEntity(GameEntity curGameEntity) {
        this.curGameEntity = curGameEntity;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public void resetMoveNumber() {
        this.moveNumber = 0;
    }

    // public int whitePoints() {
    // return whiteTerritory.size();
    // }

}
