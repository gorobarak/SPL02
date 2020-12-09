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
	
  
    public Ewok (int serialNumber){
        this.serialNumber = serialNumber;
        available = true;
    }
	/**
     * Acquires an Ewok
     */
    public synchronized void acquire() { //blocking, waits if not available
        while (!available){
            try {
                wait();
            } catch (InterruptedException e){}
        }
        available = false;
    }

    /**
     * release an Ewok
     */
    public synchronized void release() {
        available = true;
        this.notifyAll();
    }

    /**
     * @return the ewok's serial number
     */
    public int getSerialNumber() {
        return serialNumber;
    }

    /**
     *
     * @return available member
     */
    public boolean getAvailability(){
        return available;
    }
}
