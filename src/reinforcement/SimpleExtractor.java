package reinforcement;

import game.Game;

public class SimpleExtractor implements FeatureExtractor {
    @Override
    public Counter<String> getFeatures(State state, Action action) {
        Counter<String> feats = new Counter<>();
        feats.put("alt", (float)Game.game.getAltitude()/Game.WINDOW_HEIGHT);
        feats.put("dx", (float)Game.lander.dx);
        feats.put("dy", (float)Game.lander.dy);
        feats.put("rot", (float)Game.game.getAngleDeg()/360f);
        feats.put("x", (float)Game.lander.x/Game.WINDOW_WIDTH);
        feats.put("y", (float)Game.lander.y/Game.WINDOW_HEIGHT);
//        feats.put("ceil", (float)Game.lander.y/Game.WINDOW_HEIGHT);
//        feats.put("far_wall", (float)Game.lander.x/Game.WINDOW_WIDTH);
//        feats.put("near_wall", (float)(Game.WINDOW_WIDTH - Game.lander.x)/Game.WINDOW_WIDTH);
        return feats;
    }
}
