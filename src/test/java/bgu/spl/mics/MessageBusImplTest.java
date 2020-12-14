package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.broadcastForTest;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class MessageBusImplTest {

    MessageBus mb;
    @BeforeEach
    void setUp() {
        mb = MessageBusImpl.getInstance();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void subscribeEvent() {
        //the subscribe event functionality is tested in sendEvent()
    }

    @Test
    void subscribeBroadcast() {
        //the subscribe event functionality is tested in sendEvent()
    }

    @Test
    void complete() {//
        MicroService m = new C3POMicroservice();
        mb.register(m);
        mb.subscribeEvent(AttackEvent.class, m);
        AttackEvent e = new AttackEvent(new Attack(new ArrayList<>(), 12));
        Future<Boolean> f = mb.sendEvent(e);
        mb.complete(e,true);
        try {
            assertTrue(f.get());
        } catch (Exception any) {fail("");}
        mb.register(m);//return mb to its initialized state

    }

    @Test
    void sendBroadcast() {//
        Broadcast b = new broadcastForTest();
        MicroService mc = new C3POMicroservice();
        MicroService mr = new R2D2Microservice(41);

        mb.register(mc);
        mb.register(mr);
        mb.subscribeBroadcast(b.getClass() ,mc);
        mb.subscribeBroadcast(b.getClass() ,mr);
        mb.sendBroadcast(b);
        try {
            assertEquals(b, mb.awaitMessage(mc));
            assertEquals(b, mb.awaitMessage(mr));
        } catch (Exception ex) {fail("broadcast wasn't received by all");}

        mb.unregister(mc);
        mb.unregister(mr); //return mb to its initialized state
    }

    @Test
    void sendEvent() {
        AttackEvent e = new AttackEvent(new Attack(new ArrayList<>(),14 ));
        MicroService m = new C3POMicroservice();
        //test that null is returned for an even with no subscribers
        assertNull(mb.sendEvent(e));


        mb.register(m);
        mb.subscribeEvent(e.getClass() ,m);
        //test that Future returned is not null if there are subscribers
        assertNotNull(mb.sendEvent(e));

        try {//test that massage arrived to the subscriber
            assertEquals(e, mb.awaitMessage(m));
        } catch (Exception ex) {fail("event didn't arrive");}

        mb.unregister(m); //return mb to its initialized state
    }

    @Test
    void register() {
        //the register functionality is tested in other functions
    }

    @Test
    void unregister() {
        // implementation dependent, no need to test
    }

    @Test
    void awaitMessage() {
        MicroService m = new C3POMicroservice();
        try {
            mb.awaitMessage(m);
            fail("exception was not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalStateException);
        }
        mb.register(m);
        mb.subscribeEvent(AttackEvent.class, m);
        AttackEvent e = new AttackEvent(new Attack(new ArrayList<>(),14 ));
        mb.sendEvent(e);
        try {
            assertEquals(e, mb.awaitMessage(m));
        } catch (Exception ex) {fail();}

        mb.unregister(m); //return mb to its initialized state
    }

}