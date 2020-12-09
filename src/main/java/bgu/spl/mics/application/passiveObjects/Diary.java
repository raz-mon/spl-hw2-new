package bgu.spl.mics.application.passiveObjects;


/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    int totalAttacks;
    long HanSoloFinish;
    long HanSoloTerminate;
    long C3POFinish;
    long C3POTerminate;
    long R2D2Deactivate;
    long R2D2Terminate;
    long LeiaTerminate;
    long LandoTerminate;

    private static Diary diary = null;

    /**
     * Returns a new instance of Ewoks if there isn't an existing one. If there is, returns it.
     * @return
     */
    public static Diary getInstance(){
        if (diary == null)
            diary = new Diary();
         return diary;
    }

    /**
     * CTR.
     */
    private Diary(){
        totalAttacks = 0;
        HanSoloFinish = 0;
        HanSoloTerminate = 0;
        C3POFinish = 0;
        C3POTerminate = 0;
        R2D2Deactivate = 0;
        R2D2Terminate = 0;
        LeiaTerminate = 0;
        LandoTerminate = 0;
    }

    public int getTotalAttacks(){
        return this.totalAttacks;
    }

    public long getHanSoloFinish(){
        return HanSoloFinish;
    }

    public long getHanSoloTerminate(){
        return HanSoloTerminate;
    }

    public long getC3POFinish(){
        return C3POFinish;
    }

    public long getC3POTerminate(){
        return C3POTerminate;
    }

    public long getR2D2Terminate(){
        return R2D2Terminate;
    }

    public long getR2D2Deactivate(){
        return R2D2Deactivate;
    }

    public long getLeiaTerminate(){
        return LeiaTerminate;
    }

    public long getLandoTerminate(){
        return LandoTerminate;
    }

    public void setTotalAttacks(int totAttacks){
        this.totalAttacks = totAttacks;
    }

    public void setHanSoloFinish(long HanSoloFinish){
        this.HanSoloFinish = HanSoloFinish;
    }

    public void setC3POFinish(long C3POFinish){
        this.C3POFinish = C3POFinish;
    }

    public void setR2D2Deactivate(long R2D2Deactivate){
        this.R2D2Deactivate = R2D2Deactivate;
    }

    public void setLeiaTerminate(long LeiaTerminate){
        this.LeiaTerminate = LeiaTerminate;
    }

    public void setHanSoloTerminate(long HanSoloTerminate){
        this.HanSoloTerminate = HanSoloTerminate;
    }

    public void setC3POTerminate(long C3POTerminate){
        this.C3POTerminate = C3POTerminate;
    }

    public void setR2D2Terminate(long R2D2Terminate){
        this.R2D2Terminate = R2D2Terminate;
    }

    public void setLandoTerminate(long LandoTerminate){
        this.LandoTerminate = LandoTerminate;
    }

    public String toString(){
        String ret = new String();
        ret = "total attacks: " + totalAttacks + "\n" + "HanSoloFinosh: " + HanSoloFinish + "\n" + "C3POFinish: " + C3POFinish  + "\n" +
            "C3POTerminate: " + C3POFinish + "\n" + "R2D2Deactivate: " + R2D2Deactivate + "\n" + "R2D2Terminate: " + R2D2Terminate + "\n" +
                "LeiaTerminate: " + LeiaTerminate + "\n" +  "LandoTerminate: "  + LandoTerminate;
        return ret;
    }
}

