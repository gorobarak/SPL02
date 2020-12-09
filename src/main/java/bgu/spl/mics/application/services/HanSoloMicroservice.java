package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }


    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class,(AttackEvent att) -> {
            List<Integer> serials = att.getSerials();
            int duration = att.getDuration();
            Ewoks ewoks = Ewoks.getInstance();
            ewoks.acquire(serials); //blocking
            try {
                sleep(duration);
            } catch (InterruptedException e) {}
            ewoks.release(serials);
        });
        subscribeBroadcast(TerminateBroadcast.class,(terminateBroadcast) -> terminate());

    }
}
