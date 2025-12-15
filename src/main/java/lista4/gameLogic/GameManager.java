package lista4.gameLogic;

import lista4.gameInterface.GameOutputAdapter;
import lista4.gameLogic.gameExceptions.GameNotRunningException;
import lista4.gameLogic.gameExceptions.OtherPlayersTurnException;
import lista4.gameLogic.state.GameState;

public class GameManager {

    public enum PlayerColor {
        BLACK,
        WHITE,
        BOTH
    }

    public static class Move {
        int x;
        int y;
        PlayerColor playerColor;

        Move(int x, int y, PlayerColor playerColor) {
            this.x = x;
            this.y = y;
            this.playerColor = playerColor;
        }
    }

    // tutaj już tworzę instancje, konstruktor jest private (wziąłem z przykładu)
    private static GameManager instance;
    private final GameContext gameContext;
    private final Board board;
    private GameOutputAdapter outAdapter; // dodałem out Adapter do gry on później wyśle result do klientów


//----------------------------------------Sekcja techniczna------------------------------------------------------

    private GameManager(GameOutputAdapter outAdapter) {
        instance = this;
        gameContext = new GameContext(GameState.GAME_NOT_RUNNING);
        board = new Board();
        this.outAdapter = outAdapter;
    }

    public static GameManager getInstance() {
        return instance;
    }
    public void setAdapter(GameOutputAdapter adapter) {
        this.outAdapter = adapter;
    }
    public GameOutputAdapter getAdapter() { return outAdapter; }
    public Board getBoard() { return board; }

//---------------------------------------Sekcja start/stop gry--------------------------------------------------

    public void startGame(){
        gameContext.startGame();
    }

    public void endGame(){
        gameContext.endGame();
    }

//---------------------------------------Sekcja ruchów----------------------------------------------------------

    //TODO - sprawdzić, czy nie trzeba będzie zrobić synchronized
    public void makeMove(Move move){
        try {
            if(gameContext.curGameState == GameState.GAME_NOT_RUNNING){
                throw new GameNotRunningException();
            }
            if(move.playerColor == PlayerColor.BLACK && gameContext.curGameState == GameState.WHITE_MOVE) {
                throw new OtherPlayersTurnException(PlayerColor.WHITE);
            }
            if(move.playerColor == PlayerColor.WHITE && gameContext.curGameState == GameState.BLACK_MOVE) {
                throw new OtherPlayersTurnException(PlayerColor.BLACK);
            }

            Stone stone = new Stone(move.x, move.y, move.playerColor);
            board.putStone(move.x, move.y, stone);
            for(int dx : new int[]{-1, 1}) {
                for(int dy : new int[]{-1, 1}) {
                    if(board.getStone(move.x + dx, move.y + dy) == null){
                        stone.addBreath(new Stone.Breath(move.x + dx, move.y + dy));
                    }
                }
            }
            outAdapter.sendBoard(board, "???Cojamamtuwpisać???", PlayerColor.BOTH);
        }
        catch (Exception e){
            outAdapter.sendExceptionMessage(e, move.playerColor);
        }

    }



    // poniżej dodałem
    public String simulateMove(String message) { // może być voidem ale to do sprawdzania na razie
        try {
            Thread.sleep(100); // tutaj gra coś sobie sprawdza
        } catch (InterruptedException e) {

        }
        outAdapter.sendBoard(board, message, PlayerColor.BOTH);
        return "moved after: 1s";
    }



    // tu koniec dodawania

}
