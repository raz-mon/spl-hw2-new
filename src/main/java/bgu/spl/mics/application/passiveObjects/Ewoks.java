package bgu.spl.mics.application.passiveObjects;

import java.util.Vector;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 */
public class Ewoks {

    private Vector<Ewok> ewoks;
    private static Ewoks ewok = null;

    /**
     * Returns a new instance of Ewoks if there isn't an existing one. If there is, returns it.
     * @param num
     * @return
     */
    public static Ewoks getInstance(int num){
        if (ewok == null){
            ewok = new Ewoks(num);
        }
        return ewok;
    }

    /**
     * CTR
     * @param num
     */
    private Ewoks(int num){
        ewoks = new Vector<Ewok>(0,1);
        for (int i=1; i<=num; i++){
            Ewok e = new Ewok(i);       // Initialize the ewok with the relevant serial number.
            ewoks.add(e);
        }
    }

    /**
     * returns the ewok with the requested i.d.
     * @param i
     * @return
     */
    public Ewok getEwok(int i){
        if(i > ewoks.size())
            throw new IndexOutOfBoundsException();
        return this.ewoks.get(i-1);         // ewok i (serial number) is placed in ewoks[i-1], because the ewoks are numbered 1,2,3...
    }

    public static void reset(){
        ewok = null;
    }

}
