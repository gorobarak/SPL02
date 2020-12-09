package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.CheckAttackStatusBroadcast;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

    private AtomicBoolean wasDeactivateSent;
    private AtomicInteger numOfAttacks;
    private AtomicInteger totalAttacks;
    public C3POMicroservice(AtomicInteger numOfAttacks,AtomicBoolean wasDeactivateSent,AtomicInteger totalAttacks) {
        super("C3PO");
        this.numOfAttacks = numOfAttacks;
        this.wasDeactivateSent = wasDeactivateSent;
        this.totalAttacks = totalAttacks;
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
            totalAttacks.incrementAndGet();
        });
        subscribeBroadcast(TerminateBroadcast.class,(terminateBroadcast) -> terminate());
        subscribeBroadcast(CheckAttackStatusBroadcast.class,(checkAttackStatusBroadcast)->{
            if(totalAttacks.equals(numOfAttacks))
        });

    }
}
