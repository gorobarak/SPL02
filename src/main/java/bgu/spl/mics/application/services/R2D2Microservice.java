package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

import static java.lang.Thread.*;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    private Diary diary = Diary.getInstance();
    private final long duration;
    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
    }



    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class,(terminateBroadcast) -> {
            diary.setR2D2Terminate(System.currentTimeMillis());
            terminate();
        });
        subscribeEvent(DeactivationEvent.class,(deactivationEvent)->{
            try {
                sleep(duration); //simulating deactivating the shield
                //TODO check if millis
            }catch (InterruptedException ignored){}
            diary.setR2D2Deactivate(System.currentTimeMillis());
            complete(deactivationEvent,true); //tell my friend i'm finished
        });
    }
}
