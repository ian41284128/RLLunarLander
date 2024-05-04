package reinforcement;

import game.Game;
import game.Lander;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Collections.min;

public class SimpleExtractor implements FeatureExtractor {
    @Override
    public Counter<String> getFeatures(State state, Action action) {
        float thrust = 0;
        float turn = 0;
        switch(action){
            case ACCELERATE:
                thrust = (float)Lander.ACCEL;
                break;
            case TURN_LEFT:
                turn = -(float)Lander.ROTATION_SPEED;
                break;
            case TURN_RIGHT:
                turn = (float)Lander.ROTATION_SPEED;
                break;
        }
        thrust *= 100;
        //turn *= 100;

        double nextRot = state.rotation + turn;
        double nextDx = (state.dx - Math.cos(nextRot) * thrust);
        double nextDy = (state.dy - Math.sin(nextRot) * thrust);
        double nextX = state.x + nextDx;
        double nextY = state.y + nextDy + Game.GRAVITY;
        boolean hitsBarrier = nextY <= 0 || nextX <= 0 || nextY >= Game.WINDOW_HEIGHT || nextX >= Game.WINDOW_WIDTH;
        float nearestBarrier = min(new ArrayList<Float>(){{
            add((float)nextX);
            add((float)nextY);
            add((float)(Game.WINDOW_WIDTH-nextX));
        }});
        Counter<String> feats = new Counter<>();
        feats.put("bias", 1f);
        feats.put("hits_barrier", hitsBarrier? 1f : 0f);
        feats.put("nearest_barrier", nearestBarrier/Math.max(Game.WINDOW_HEIGHT, Game.WINDOW_WIDTH));
        feats.put("landing_position", nextRot > 0 && nextRot < Math.toDegrees(2.5d) ? 1f : 0f);
        feats.put("landing_speed", nextDy * 100 < Game.LANDING_SPEED ? 1f : 0f);
        feats.put("altitude", (float)(Game.getY((int)nextX) - nextY) / Game.WINDOW_HEIGHT);
        return feats;
    }
}
