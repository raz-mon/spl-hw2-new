package bgu.spl.mics.application.passiveObjects;

import java.util.List;


/**
 * Passive data-object representing an attack object.
 * You must not alter any of the given public methods of this class.
 * <p>
 * Do not add any additional members/method to this class (except for getters).
 */
public class Attack {
    final List<Integer> serials;
    final int duration;

    /**
     * Constructor.
     */
    public Attack(List<Integer> serialNumbers, int duration) {
        this.serials = serialNumbers;
        this.duration = duration;
        serials.sort((a , b) -> { return a - b;});                  // Sorting the list with comparator in order to avoid dead-locks.
    }

    /**
     * getter for Serials
     * @return List<Integer>
     */
    public List<Integer> getSerials(){
        return this.serials;
    }

    /**
     * getter for duration
     * @return
     */
    public int getDuration(){
        return this.duration;
    }

    public String toString() {
        return "serials: " + serials.toString() + ", " + "duration: " + duration;
    }
}