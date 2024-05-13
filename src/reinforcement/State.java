package reinforcement;

import game.Game;

import java.util.Arrays;

public class State {
    public int fuel;
    public int rotation;
    public int x;
    public int y;
    public int dx;
    public int dy;
    public boolean terminalState = false;
    public boolean goalStateReached = false;
    public static long stateSpaceSize = (long) Game.WINDOW_HEIGHT * Game.WINDOW_WIDTH * 360 * 200;

    public State(float x, float y, float dx, float dy, float fuel, float angle){
        this.fuel = (int) fuel;
        this.rotation = (int) angle;
        this.x = (int)x;
        this.y = (int)y;
        this.dx = (int)(dx*1000);
        this.dy = (int)(dy*1000);
    }

    public State(boolean won){
        terminalState = true;
        this.goalStateReached = won;
    }

    protected int[] getValues(){
        if(terminalState){
            return new int[]{goalStateReached ? 1 : 0};
        }
        return new int[]{x, y, dx, dy, rotation};
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getValues());
    }

    @Override
    public String toString() {
        if(terminalState)
            return goalStateReached ? "WIN_STATE" : "LOSE_STATE";
        return String.format("(x:%d y:%d dx:%d, dy:%d rot:%d)", x, y, dx, dy, rotation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return fuel == state.fuel && rotation == state.rotation && x == state.x && y == state.y && dx == state.dx && dy == state.dy && terminalState == state.terminalState && goalStateReached == state.goalStateReached;
    }
}
