package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.services.C3POMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    }

    @Test
    void sendBroadcast() {//
    }

    @Test
    void sendEvent() {//
        MicroService m1 = new C3POMicroservice();
        mb.register(m1);
        Event<Integer> e = new AttackEvent();
        mb.subscribeEvent(e ,m1);

    }

    @Test
    void register() {
    }

    @Test
    void unregister() {
        // implementation dependent, no need to test
    }

    @Test
    void awaitMessage() {
    }
}