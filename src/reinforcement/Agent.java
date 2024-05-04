package reinforcement;

public class Agent {
    Counter<Tuple<State, Action>> Q = new Counter<>();
    public int episodes = 0;
    //Exploration prob
    public float epsilon = 0.05f;
    //learning rate
    public float alpha = 0.025f;
    //discount rate
    public float gamma = 0.01f;
    public int trainingEpisodes = 2000;

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
            float qValue = getQValue(state, action);
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
        Q.put(state, action, (1-alpha) * getQValue(state, action) + alpha * sample);
    }

    public Action[] getLegalActions(State state){
        if(state.fuel > 0)
            return Action.values();

        return new Action[]{Action.TURN_RIGHT, Action.TURN_LEFT, Action.NO_ACTION};
    }

    public void transition(State state, Action action, State nextState, float reward){
        update(state, action, nextState, reward);
        if(nextState.terminalState){
            if(reward > 0)
                episodes++;
            System.out.println("Episode " + episodes + ": " + (((float)Q.size()/State.stateSpaceSize)*100));
        }
    }
}