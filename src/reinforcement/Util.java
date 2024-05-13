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
        return getRandom().nextFloat() < p;
    }

    public static boolean flipCoin(){
        return getRandom().nextBoolean();
    }

    public static <T>T choice(T[] seq){
        if(seq.length == 0)
            throw new IllegalStateException("Array cannot be empty.");
        return seq[getRandom().nextInt(seq.length)];
    }

    public static float dist(float x1, float y1, float x2, float y2){
        return (float)Math.abs(Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2)));
    }
}
