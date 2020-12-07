package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import bgu.spl.mics.application.messages.ExplotionBroadcast;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.services.HanSoloMicroservice;


class MessageBusImplTest{

    private MessageBusImpl msgbus;

    @BeforeEach
    void setUp() {
        msgbus = MessageBusImpl.getInstance();
    }

    @Test
    void testsubscribeEvent() {
        MicroService m1 = new HanSoloMicroservice();
        AttackEvent attack = new AttackEvent();
        DeactivationEvent DE = new DeactivationEvent();
        msgbus.register(m1);
        m1.subscribeEvent(attack.getClass(), (atk) -> {});
        msgbus.sendEvent(DE);
        msgbus.sendEvent(attack);
        try {
            AttackEvent a1 = (AttackEvent) msgbus.awaitMessage(m1);
            assertEquals(attack, a1);
        }
        catch(Exception w){
            System.out.println("problem in awaitMessage from testsubscribeEvent");
        }
    }

    @Test
    void testsubscribeBroadcast() {
        MicroService m1 = new HanSoloMicroservice();
        MicroService m2 = new C3POMicroservice();
        // Maybe add another micro-service that isn't subscribed and make sure that he doesn't get the  message. (to avoid endless loop, it can be subscribed to another croadcast and we can make sure it gets that one and not the rather.
        msgbus.register(m1);
        msgbus.register(m2);
        m1.subscribeBroadcast(ExplotionBroadcast.class, (br) -> {});
        m2.subscribeBroadcast(ExplotionBroadcast.class, (br) -> {});
        Broadcast brd = new ExplotionBroadcast();
        msgbus.sendBroadcast(brd);
        try {
            ExplotionBroadcast exp1 = (ExplotionBroadcast) msgbus.awaitMessage(m1);
            ExplotionBroadcast exp2 = (ExplotionBroadcast) msgbus.awaitMessage(m2);
            assertEquals(exp1, brd);
            assertEquals(exp2, brd);
        }
        catch(Exception w){
            System.out.println("problem in awaitMessage from testsubscribeBroadcast");
        }
    }

    @Test
    void testcomplete() {
        Attack[] a = new Attack[0];
        MicroService m = new LeiaMicroservice(a);
        AttackEvent attack = new AttackEvent();
        Future<Boolean> ftr = m.sendEvent(attack);
        msgbus.complete(attack,true);
        assertTrue(ftr.get());
    }

    @Test
    void testsendBroadcast() {
        MicroService m1 = new HanSoloMicroservice();
        MicroService m2 = new C3POMicroservice();
        // Maybe add another micro-service that isn't subscribed and make sure that he doesn't get the  message. (to avoid endless loop, it can be subscribed to another croadcast and we can make sure it gets that one and not the rather.
        msgbus.register(m1);
        msgbus.register(m2);
        m1.subscribeBroadcast(ExplotionBroadcast.class, (br)->{});
        m2.subscribeBroadcast(ExplotionBroadcast.class, (br) -> {});
        Broadcast brd = new ExplotionBroadcast();
        msgbus.sendBroadcast(brd);
        try {
            ExplotionBroadcast exp1 = (ExplotionBroadcast) msgbus.awaitMessage(m1);
            ExplotionBroadcast exp2 = (ExplotionBroadcast) msgbus.awaitMessage(m2);
            assertEquals(exp1, brd);
            assertEquals(exp2, brd);
        }
        catch(Exception w){
            System.out.println("problem in awaitMessage from testsendBroadcast");
        }


    }

    @Test
    void testsendEvent(){
        MicroService m1 = new HanSoloMicroservice();
        MicroService m2 = new LeiaMicroservice(new Attack[0]);
        AttackEvent attack = new AttackEvent();
        msgbus.register(m1);
        msgbus.register(m2);
        m1.subscribeEvent(attack.getClass(), (atk) -> {});
        m2.sendEvent(attack);
        try{
            AttackEvent a1 = (AttackEvent) msgbus.awaitMessage(m1);
            assertEquals(attack, a1);
        }
        catch(Exception e){
            System.out.println("problem in awaitMessage from testsendEvent");
        }
    }

    @Test
    void testregister(){
        MicroService m1 = new HanSoloMicroservice();
        msgbus.register(m1);
        AttackEvent attack = new AttackEvent();
        m1.subscribeEvent(attack.getClass(), (atk) -> {});
        msgbus.sendEvent(attack);
        try {
            AttackEvent message = (AttackEvent) msgbus.awaitMessage(m1);
            assertEquals(attack, message);
        }
        catch(Exception e){
            System.out.println("problem in awaitMessage from testregister");
        }
    }

    @Test
    void testawaitMessage(){
        MicroService m1 = new HanSoloMicroservice();
        MicroService m2 = new HanSoloMicroservice();
        msgbus.register(m1);
        AttackEvent attack = new AttackEvent();
        m1.subscribeEvent(attack.getClass(), (atk) -> {});
        msgbus.sendEvent(attack);
        try {
            AttackEvent message1 = (AttackEvent)msgbus.awaitMessage(m1);
            assertEquals(message1 , attack);
        }
        catch(Exception e) {
            System.out.println("problem in awaitMessage from testawaitMessage");
        }
        boolean b = false;
        try{
            AttackEvent message2 = (AttackEvent)msgbus.awaitMessage(m2);
        }
        catch(Exception e){
            b = true;           //Make sure we get into this catch (threw an exception).
        }
        assertTrue(b);
    }
}
