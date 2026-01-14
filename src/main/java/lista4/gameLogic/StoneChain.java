package lista4.gameLogic;

import java.util.HashSet;
import java.util.Set;

public class StoneChain {
    private final Set<Stone> stones = new HashSet<>();

    public StoneChain(Stone initial) {
        addStone(initial);
    }

    public void addStone(Stone s) {
        stones.add(s);
        s.setChain(this);
    }

    public void merge(StoneChain other) {
        for (Stone s : other.stones) addStone(s);
    }

    public int getBreathCount() {
        Set<Field> breaths = new HashSet<>();
        for (Stone stoneElement : stones) {
            breaths.addAll(stoneElement.getBreaths());
        }
        return breaths.size();
    }

    public boolean isDead() {
        return getBreathCount() == 0;
    }

    public void captureChain(){
        for(Stone stone : stones){
            int x = stone.getX();
            int y = stone.getY();
            stone.getBoard().removeStone(x, y);
        }
    }

    public Set<Stone> getStones() { return stones; }
}
