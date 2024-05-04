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
    public void update(State state, Action action, State nextState, float reward) {
        Counter<String> features = featExtractor.getFeatures(state, action);
        float diff = (reward + gamma * computeValueFromQValues(nextState)) - getQValue(state, action);
        for(String key : features.keySet()){
            getWeights().put(key, getWeights().get(key) + alpha * diff * features.get(key));
        }
    }

    @Override
    public void transition(State state, Action action, State nextState, float reward) {
        update(state, action, nextState, reward);
        if(nextState.terminalState) {
            episodes++;
            System.out.println("episode: " + episodes + " " + getWeights());
        }
    }
}
