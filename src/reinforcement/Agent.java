package reinforcement;

import game.Game;
import game.Score;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Agent {
    Counter<Tuple<State, Action>> Q = new Counter<>();
    Counter<Tuple<State, Action>> N = new Counter<>(1);
    ArrayList<Tuple<State, Action>> episodeHistory = new ArrayList<>();
    public int incentive = 0;
    //Exploration prob
    public float epsilon = 0.01f;
    //learning rate
    public float alpha = 0.005f;
    //discount rate
    public float gamma = 0.5f;
    public int trainingEpisodes = 100;

    public int episodes = 0;
    public float averageReward = 0;

    public static void main(String[] args) {
        game.Game.main(new String[]{"--ai-agent"});
    }

    public float getQValue(State state, Action action){
        return Q.get(state, action);
    }

    public float computeValueFromQValues(State state){
        float bestQ = Float.NEGATIVE_INFINITY;
        for (Action action : getLegalActions(state)){
            bestQ = Math.max(getQValue(state, action), bestQ);
        }
        if(Float.isInfinite(bestQ))
            return 0f;
        return bestQ;
    }

    public Action computeActionFromQValues(State state){
        Action bestAction = null;
        float bestQ = Float.NEGATIVE_INFINITY;
        for (Action action : getLegalActions(state)){
            float qValue = getQValue(state, action) + getExploreIncentive(state, action);
                                //breaks ties randomly
            if(qValue > bestQ || (qValue == bestQ && Util.flipCoin())){
                bestQ = qValue;
                bestAction = action;
            }
        }
        return bestAction;
    }

    public Action getAction(State state){
        Action[] legalActions = getLegalActions(state);
        if(Util.flipCoin(epsilon) && legalActions.length > 0){
            return Util.choice(legalActions);
        } else {
            return computeActionFromQValues(state);
        }
    }

    public void update(State state, Action action, State nextState, float reward){
        float sample = reward + gamma * computeValueFromQValues(nextState);
        float newVal = (1-alpha) * getQValue(state, action) + alpha * sample;
        Q.put(state, action, newVal);
        N.put(state, action,  N.get(state, action) + 1);
    }

    public Action[] getLegalActions(State state){
        //return new Action[]{Action.ACCELERATE, Action.NO_ACTION};
        if(state.fuel > 0)
            return Action.values();

        return new Action[]{Action.TURN_RIGHT, Action.TURN_LEFT, Action.NO_ACTION};
    }

    public float getReward(State state, Action action){
        if(state.terminalState){
            int points = Game.getPoints();
            if(points == 0) {
                return -100;
            }
            return points;
        }
        return 0;
//        if(action == null)
//            return 0;
//        switch (action){
//            case ACCELERATE:
//            case TURN_R_ACCEL:
//            case TURN_L_ACCEL:
//                return -1;
//            default:
//                return 0;
//        }
    }

    public float getExploreIncentive(State state, Action action){
        float visits = N.get(state, action);
        return incentive/visits;
    }

    public void backPropagateReward(float reward){
        for(int i = episodeHistory.size(); i > 0; i--){

        }
    }

    public void transition(State state, Action action, State nextState){
        float reward = getReward(nextState, action);
        episodeHistory.add(new Tuple<>(state, action));
        update(state, action, nextState, reward);
        if(nextState.terminalState){
            episodeHistory.clear();
            episodes++;
            averageReward += reward;
            if(episodes % 100 == 0){
                System.out.println("Episode " + episodes + ": Average reward " + averageReward/100);
                averageReward = 0;
            }
            //System.out.println("Episode " + episodes);
            if(episodes == trainingEpisodes){
                ArrayList<Float> values = new ArrayList<>(Q.values());
                Collections.sort(values);
                System.out.println(values);
            }
        }
        if(episodes >= trainingEpisodes){
            System.out.println(state + " " + action + " " + " " + nextState + " " + getQValue(state, action));
        }
    }
}