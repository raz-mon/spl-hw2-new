package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
	int serialNumber;
	boolean available;
    public Ewok(int serialNumber){
      available = true;
        this.serialNumber = serialNumber;
    }
    /**
     * Acquires an Ewok
     */
    public synchronized void acquire() {
		this.available = false;
    }

    /**
     * release an Ewok
     */
    public synchronized void release() {
        this.available = true;
    	this.notifyAll();
    }



    /**
     * @return serialNumber of Ewok
     */
    public int getSerialNumber(){
        return this.serialNumber;
    }


   // ------------------------------ from here on - this is only for our internal testing.

    /**
     * This function is only for testing!!
     * @return
     */
    public boolean isAvailable(){
        return available;
    }
}

