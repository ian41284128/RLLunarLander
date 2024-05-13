package reinforcement;

import java.util.Objects;

public class Tuple<State, Action> {
    public State state;
    public Action action;

    public Tuple(State state, Action action){
        this.state = state;
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return tuple.state.equals(state) && tuple.action.equals(action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, action);
    }

    @Override
    public String toString() {
        return "{" +
                "state=" + state +
                ", action=" + action +
                '}';
    }
}
