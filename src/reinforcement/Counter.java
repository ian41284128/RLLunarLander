package reinforcement;
import java.util.Hashtable;

public class Counter<K> extends Hashtable<K, Float> {
    @Override
    public synchronized Float get(Object key) {
        if (super.containsKey(key))
            return super.get(key);
        return 0f;
    }

    public synchronized Float get(State state, Action action){
        return get(new Tuple<>(state, action));
    }

    public synchronized void put(State state, Action action, float value){
        put((K) new Tuple<>(state, action), value);
    }
}
