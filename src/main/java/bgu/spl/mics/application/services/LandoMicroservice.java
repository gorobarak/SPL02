package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

import static java.lang.Thread.sleep;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    private final long duration;
    private Diary diary = Diary.getInstance();

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class,(terminateBroadcast) -> {
            diary.setLandoTerminate(System.currentTimeMillis());
            terminate();});
        subscribeEvent(BombDestroyerEvent.class,(bombDestroyerEvent)->{
            try {
                sleep(duration); //simulating bombing the star destroyer.
            }catch (InterruptedException ignored){}
            sendBroadcast(new TerminateBroadcast()); //all is done signal all the terminate.
        });
       
    }
}
