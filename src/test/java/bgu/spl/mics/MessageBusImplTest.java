package bgu.spl.mics;

import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import bgu.spl.mics.application.messages.ExplotionBroadcast;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.services.HanSoloMicroservice;


class MessageBusImplTest{

    private MessageBusImpl msgbus;
    private MicroService m1;
    private MicroService m2;

    @BeforeEach
    void setUp() {
        msgbus = MessageBusImpl.getInstance();
        m1 = new HanSoloMicroservice();
        m2 = new C3POMicroservice();
    }

    @AfterEach
    void tearDown() {
        msgbus.unregister(m1);
        msgbus.unregister(m2);

    }

    @Test
    void testsubscribeEvent() {
        BombDestroyerEvent bomb = new BombDestroyerEvent();
        DeactivationEvent DE = new DeactivationEvent();
        msgbus.register(m1);
        m1.subscribeEvent(bomb.getClass(), (bombardment) -> {});
        msgbus.sendEvent(bomb);
        msgbus.sendEvent(DE);
        try {
            AttackEvent a1 = (AttackEvent) msgbus.awaitMessage(m1);
            assertEquals(bomb, a1);
        }
        catch(Exception w){
            System.out.println("problem in awaitMessage from testsubscribeEvent");
        }
    }

    @Test
    void testsubscribeBroadcast() {
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
        msgbus.register(m1);
        DeactivationEvent deact = new DeactivationEvent();
        m1.subscribeEvent(DeactivationEvent.class, (deactivate)->{});
        Future<Boolean> ftr = msgbus.sendEvent(deact);
        msgbus.complete(deact,true);
        assertTrue(ftr.get());
    }

    @Test
    void testsendBroadcast() {
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
        msgbus.register(m1);
        DeactivationEvent deact = new DeactivationEvent();
        m1.subscribeEvent(deact.getClass(), (deactivate) -> {});
        msgbus.sendEvent(deact);
        try {
            DeactivationEvent message = (DeactivationEvent) msgbus.awaitMessage(m1);
            assertEquals(deact, message);
        }
        catch(Exception e){
            System.out.println("problem in awaitMessage from testregister");
        }
    }

    @Test
    void testawaitMessage(){
        msgbus.register(m1);
        DeactivationEvent deact = new DeactivationEvent();
        m1.subscribeEvent(deact.getClass(), (atk) -> {});
        msgbus.sendEvent(deact);
        try {
            AttackEvent message1 = (AttackEvent)msgbus.awaitMessage(m1);
            assertEquals(message1 , deact);
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
