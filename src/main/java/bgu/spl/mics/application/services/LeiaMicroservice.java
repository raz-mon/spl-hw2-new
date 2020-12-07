package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import java.util.concurrent.atomic.AtomicInteger;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.ExplotionBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private Future<Boolean>[] ftr;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        this.ftr = new Future[this.attacks.length + 2];           // two additional slots: 1 for DeactivationEvent and one for BombDestroyerEvent
    }

    /**
     * initialization for Leia:
     * register , send eventAttacks and subscribe to a Broadcast in messageBus
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(ExplotionBroadcast.class, (exp) -> {diary.setLeiaTerminate(System.currentTimeMillis());
            terminate();});
        try {
            Thread.sleep(200);      //Let other Micro-sevices register and subscribe.
        }catch (Exception e){ System.out.println("problem with sleep - Leia"); }
        //Send Events
        for(int i = 0; i < attacks.length; i++){
            AttackEvent e = new AttackEvent(attacks[i]);
            ftr[i] = sendEvent(e);
        }
        int i = 0;
        //wait till all of leia's attackEvent's futures are resolved.
        while (i < ftr.length - 2){
            ftr[i].get();
            i++;
        }
        DeactivationEvent deactEve = new DeactivationEvent();
        ftr[ftr.length - 2] = sendEvent(deactEve);                  // sending deactivation event to R2D2 via message bus
        ftr[ftr.length - 2].get();                                  //wait till R2D2 finish deactivating ship's shield

        BombDestroyerEvent bombardment = new BombDestroyerEvent();
        ftr[ftr.length - 1] = sendEvent(bombardment);               //sending bombdestroyer event to Lando via message bus
        ftr[ftr.length - 1].get();                                  //wait till lando will destroy the ship

        sendBroadcast(new ExplotionBroadcast());                    //the bad guys are dead and the ship exploded, send everyone a broadcast
    }
}
