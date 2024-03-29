package bgu.spl.mics.application.passiveObjects;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static class instanceHolder {
        private static Ewoks instance = new Ewoks();
    }
    private List<Ewok> ewoks;


    private Ewoks(){}

    public void init(int numOfEwoks){
        ewoks = new ArrayList<>();
        for (int i = 0; i < numOfEwoks; i++) {
             ewoks.add(new Ewok(i+1));
        }
    }
    public void acquire (List<Integer> serials){ //assume serials are sorted
        for (int i = 0; i < serials.size() ; i++) { //acquire resources in order
            ewoks.get(serials.get(i) - 1).acquire(); //blocking
        }
    }
    public void release (List<Integer> serials){// assume serials are sorted
        for (int i = 0; i < serials.size(); i++) {
            ewoks.get(serials.get(i) - 1).release();
        }

    }
    public static Ewoks getInstance() {
        return instanceHolder.instance;
    }
}
