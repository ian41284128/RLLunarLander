package reinforcement;

import java.util.Arrays;

public class State {
    public int altitude;
    public int velocity;
    public int fuel;
    public int rotation;
    public int x;

    public State(float altitude, float velocity, float fuel, float angle, double x){
        this.altitude = (int) altitude;
        this.velocity = (int) velocity;
        this.fuel = (int) fuel;
        this.rotation = (int) angle;
        this.x = (int)x;


    }

    protected int[] getValues(){
        return new int[]{altitude, velocity, rotation, x};
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getValues());
    }

    @Override
    public String toString() {
        return String.format("(a:%d v:%d r:%d)", altitude, velocity, rotation);
    }
}
