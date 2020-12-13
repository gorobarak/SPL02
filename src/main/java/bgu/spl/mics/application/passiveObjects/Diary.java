package bgu.spl.mics.application.passiveObjects;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {

    private static class instanceHolder {
        private static Diary instance = new Diary();
    }
    AtomicInteger totalAttacks; //will be updated by HanSolo and C3PO only
    long HanSoloFinish;
    long C3POFinish;
    long R2D2Deactivate;
    long LeiaTerminate;
    long HanSoloTerminate;
    long C3POTerminate;
    long R2D2Terminate;
    long LandoTerminate;

    private Diary(){

    }
    public void initTotalAttacks(AtomicInteger totalAttacks) {
        this.totalAttacks = totalAttacks;
    }//TODO should be called in main

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        R2D2Deactivate = r2D2Deactivate;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }

    public static Diary getInstance(){
        return instanceHolder.instance;
    }

}
