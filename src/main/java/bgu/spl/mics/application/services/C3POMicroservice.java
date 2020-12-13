package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
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

    private Object lock;
    private AtomicBoolean wasDeactivateSent;
    private AtomicInteger numOfAttacks;
    private AtomicInteger totalAttacks;
    private Diary diary = Diary.getInstance();
    public C3POMicroservice() {
        super("C3PO");

    }
    public void init(AtomicInteger numOfAttacks,AtomicBoolean wasDeactivateSent,AtomicInteger totalAttacks, Object lock){
        this.numOfAttacks = numOfAttacks;
        this.wasDeactivateSent = wasDeactivateSent;
        this.totalAttacks = totalAttacks;
        this.lock = lock;
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
        subscribeBroadcast(TerminateBroadcast.class,(terminateBroadcast) -> {
            diary.setC3POTerminate(System.currentTimeMillis());
            terminate();
        });
        subscribeBroadcast(CheckAttackStatusBroadcast.class,(checkAttackStatusBroadcast)->{
            diary.setC3POFinish(System.currentTimeMillis());
            synchronized (lock) {
                if (totalAttacks.get() == numOfAttacks.get() && !wasDeactivateSent.get()) {
                    Future<Boolean> future = sendEvent(new DeactivationEvent());
                    future.get(); //blocking until R2D2 finishes deactivating the shield
                    sendEvent(new BombDestroyerEvent());
                }
            }
        });

    }
}
