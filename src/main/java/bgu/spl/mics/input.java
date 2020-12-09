package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Attack;

public class input {
    private Attack[] attacks;
    private long R2D2;
    private long Lando;
    private int Ewoks;

    /**
     * Empty CTR.
     */
    public input() { }

    /**
     * return the attacks field.
     * @return
     */
    public Attack[] getAttacks(){
        for(int i=0; i<attacks.length; i++){
            attacks[i].getSerials().sort((a,b) -> {return a-b;});       // Sort the serials at the attacks recieved (they may be un-ordered). This prevents dead-locks later on.
        }
        return attacks;
    }

    public long getR2D2(){
        return this.R2D2;
    }
    public long getLando(){
        return this.Lando;
    }
    public int getEwoks(){
        return this.Ewoks;
    }

    /**
     * This is mainly for testing pruposes. Yet is relevant for the class.
     * @return
     */
    public String toString(){
        String out = "";
        for(int i=0; i<attacks.length; i++){
            out = out + attacks[i].toString();
            if (i!=attacks.length-1)
                out = out + "\n";
        }
        out = out + "\n" + "R2D2: " + R2D2 + "\n" + "Lando: " + Lando + "\n" + "Ewoks: " + Ewoks;
        return out;
    }
}
