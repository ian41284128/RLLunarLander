package reinforcement;

public interface FeatureExtractor {
    Counter<String> getFeatures(State state, Action action);
}
