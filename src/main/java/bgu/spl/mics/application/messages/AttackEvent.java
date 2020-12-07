package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

public class AttackEvent implements Event<Boolean> {
    Attack atk;

    /**
     * Empty constructor
     */
    public AttackEvent(){}

    /**
     * constructor
     * @param atk
     */
    public AttackEvent(Attack atk){
        this.atk = atk;
    }

    /**
     * getter to atk
     * @return Attack
     */
    public Attack getAttack(){
        return this.atk;
    }
}
