package reinforcement;
import java.util.Hashtable;

public class Counter<K> extends Hashtable<K, Float> {
    private float defaultValue = 0;

    public Counter(){
        super();
    }

    public Counter(float defaultValue){
        super();
        this.defaultValue = defaultValue;
    }

    @Override
    public synchronized Float get(Object key) {
        if (super.containsKey(key))
            return super.get(key);
        return defaultValue;
    }

    public synchronized Float get(State state, Action action){
        return get(new Tuple<>(state, action));
    }

    public synchronized void put(State state, Action action, float value){
        put((K) new Tuple<>(state, action), value);
    }

    @Override
    public synchronized String toString() {
        StringBuilder result = new StringBuilder("{");
        for(K key : keySet()){
            result.append(key).append(": ").append(get(key)).append(", ");
        }
        return result.append("}").toString();
    }
}
