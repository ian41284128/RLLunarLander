package reinforcement;

import game.Game;
import game.Lander;
import game.Score;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Collections.min;

public class SimpleExtractor implements FeatureExtractor {
    @Override
    public Counter<String> getFeatures(State state, Action action) {
        float thrust = 0;
        float turn = 0;
        if(action == Action.ACCELERATE || action == Action.TURN_L_ACCEL || action == Action.TURN_R_ACCEL)
            thrust = (float)Lander.ACCEL*1000;
        if(action == Action.TURN_LEFT || action == Action.TURN_L_ACCEL)
            turn = -(float)Math.toDegrees(Lander.ROTATION_SPEED);
        if(action == Action.TURN_RIGHT || action == Action.TURN_R_ACCEL)
            turn = (float)Math.toDegrees(Lander.ROTATION_SPEED);

        double nextRot = state.rotation + turn;
        double nextDx = (state.dx - Math.cos(Math.toRadians(nextRot)) * thrust);
        double nextDy = (state.dy - Math.sin(Math.toRadians(nextRot)) * thrust) + Game.GRAVITY * 1000;
        double nextX = state.x + nextDx/1000;
        double nextY = state.y + nextDy/1000;
        boolean hitsBarrier = nextY <= 0 || nextX <= 0 || nextX >= Game.WINDOW_WIDTH;
        float nearestBarrier = min(new ArrayList<Float>(){{
            add((float)nextX);
            add((float)nextY);
            add((float)(Game.WINDOW_WIDTH-nextX));
        }});

        float minScoreDist = Float.POSITIVE_INFINITY;
        float scoreLength = 0;
        for(Score score : Game.scores){
            //float dist = Util.dist((float)nextX, (float)nextY, score.x, score.y);
            float dist = (float)Math.abs(nextX-(score.x+score.length/2f));
            if(dist < minScoreDist){
                minScoreDist = dist;
                scoreLength = score.length;
            }
        }

        double distToRot = Math.toRadians(nextRot) - Math.toRadians(90);
        float distanceToLandingAngle = (float)Math.abs(Math.atan2(Math.sin(distToRot), Math.cos(distToRot)));
        float speedDiff = (float)Math.abs(nextDy) - Game.LANDING_SPEED * 10;
        if(nextDy < Game.LANDING_SPEED*10)
            speedDiff = 0;


        Counter<String> feats = new Counter<>();
//        feats.put("bias", 1f);
        //feats.put("hits_barrier", hitsBarrier? 1f : 0f);
        feats.put("nearest_barrier", nearestBarrier/Math.max(Game.WINDOW_HEIGHT, Game.WINDOW_WIDTH));
        feats.put("nearest_zone", minScoreDist/Game.WINDOW_WIDTH);
        feats.put("above_zone", minScoreDist < scoreLength/2f ? 1f : 0f);
        //feats.put("dist_to_landing_rotation", distanceToLandingAngle);
        feats.put("landing_speed", speedDiff/200);
        feats.put("altitude", (float)(Game.getY((int)nextX) - nextY) / Game.WINDOW_HEIGHT);
        feats.put("delta_y", (float)nextDy/200);
        feats.put("delta_x", (float)Math.abs(nextDx)/200);
        //feats.put("fuel", (float)state.fuel/1000);
        return feats;
    }
}
