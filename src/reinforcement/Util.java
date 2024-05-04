package reinforcement;

import java.util.Random;

public class Util {
    private static Random random;

    private static Random getRandom(){
        if(random == null)
            random = new Random();
        return random;
    }

    public static boolean flipCoin(float p){
        return getRandom().nextFloat() <= p;
    }

    public static boolean flipCoin(){
        return getRandom().nextBoolean();
    }

    public static <T>T choice(T[] seq){
        if(seq.length == 0)
            throw new IllegalStateException("Array cannot be empty.");
        return seq[getRandom().nextInt(seq.length)];
    }
}
