package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Attack;

public class input {
    private Attack[] attacks;
    private long R2D2;
    private long Lando;
    private int Ewoks;

    public input() {
    }

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

    public Attack[] getAttacks(){
        for(int i=0; i<attacks.length; i++){
            attacks[i].getSerials().sort((a,b) -> {return a-b;});
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
}
