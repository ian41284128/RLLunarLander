package reinforcement;

public class ApproximateAgent extends Agent{
    private FeatureExtractor featExtractor;
    private Counter<String> weights;

    public ApproximateAgent(){
        featExtractor = new SimpleExtractor();
        weights = new Counter<>(1);
    }

    public Counter<String> getWeights(){
        return weights;
    }

    @Override
    public float getQValue(State state, Action action) {
        float sum = 0;
        Counter<String> features = featExtractor.getFeatures(state, action);
        for(String key : features.keySet()){
            sum += getWeights().get(key) * features.get(key);
        }
        return sum;
    }

    @Override
    public Action computeActionFromQValues(State state) {
        int originalRotation = state.rotation;
        int maxValueRotation = originalRotation;
        float maxQ = Math.max(getQValue(state, Action.ACCELERATE), getQValue(state, Action.NO_ACTION));
        for(state.rotation = 0; state.rotation < 360; state.rotation += 10){
            float qValue = getQValue(state, Action.ACCELERATE);
            if(qValue > maxQ){
                maxValueRotation = state.rotation;
                maxQ = qValue;
            }
        }
        state.rotation = originalRotation;
        double distToRot = Math.toRadians(originalRotation) - Math.toRadians(maxValueRotation);
        int dir = (int)Math.signum(Math.atan2(Math.sin(distToRot), Math.cos(distToRot)));
        Action bestTurnAction = Action.NO_ACTION;
        switch (dir){
            case 1:
                bestTurnAction = Action.TURN_LEFT;
                break;
            case -1:
                bestTurnAction = Action.TURN_RIGHT;
                break;
        }
        if(getQValue(state, Action.ACCELERATE) > getQValue(state, Action.NO_ACTION)){
            switch (bestTurnAction){
                case TURN_LEFT:
                    return Action.TURN_L_ACCEL;
                case TURN_RIGHT:
                    return Action.TURN_R_ACCEL;
                default:
                    return Action.ACCELERATE;
            }
        }else return bestTurnAction;
    }

    @Override
    public void update(State state, Action action, State nextState, float reward) {
        Counter<String> features = featExtractor.getFeatures(state, action);
        float diff = (reward + gamma * computeValueFromQValues(nextState)) - getQValue(state, action);
        for(String key : features.keySet()){
            getWeights().put(key, getWeights().get(key) + alpha * diff * features.get(key));
        }
    }

    public void update(State state, Action action, float reward, float discount){
        Counter<String> features = featExtractor.getFeatures(state, action);
        float diff = reward - getQValue(state, action);
        for(String key : features.keySet()){
            float newVal = getWeights().get(key) + alpha * discount * diff * features.get(key);
            getWeights().put(key, newVal);
        }
    }

    @Override
    public void backPropagateReward(float reward) {
        float discount = gamma;
        for(int i = episodeHistory.size()-1; i > 0; i--){
            Tuple<State, Action> step = episodeHistory.get(i);
            update(step.state, step.action, reward, discount);
            discount *= gamma;
        }
    }

    @Override
    public void transition(State state, Action action, State nextState) {
        float reward = getReward(nextState, action);
        episodeHistory.add(new Tuple<>(state, action));
        update(state, action, nextState, reward);
        if(nextState.terminalState){
            //backPropagateReward(reward);
            episodeHistory.clear();
            episodes++;
            averageReward += reward;
            if(episodes % 100 == 0){
                System.out.println("Episode " + episodes + ": Average reward " + averageReward/100);
                System.out.println(getWeights());
                averageReward = 0;
            }
            System.out.println("episode " + episodes + "("+reward+"): " + getWeights());
        }
    }
}
