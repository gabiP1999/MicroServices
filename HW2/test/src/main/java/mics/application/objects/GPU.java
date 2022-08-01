package mics.application.objects;

//import com.sun.deploy.security.ValidationState;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    private DataBatch current_dataBatch;
    public boolean isNull(){
        return current_dataBatch==null;
    }

    public int getClock() {
        return clock;
    }
    //private int gets;
    //private int sets;
    private int timeUse;


    public enum Type {RTX3090, RTX2080, GTX1080}
    private Type type;
    private int clock;
    private int processEndTime;

    public GPU(Type type){
        this.type = type;
        clock=0;
        //gets=0;
        //sets=0;
        processEndTime=0;
        current_dataBatch=null;
        timeUse=0;
    }
    public int getTimeUse(){return timeUse;}
    public void tick(){
        clock++;
        if(!isNull())timeUse++;
        //System.out.println("Gets:"+gets+" Sets:"+sets);
    }
    public void setCurrent_dataBatch(DataBatch d,int ticks){
        //sets++;
        timeUse++;
        current_dataBatch=d;
        processEndTime=clock+ticks;
    }
    public boolean isReady(){
        //System.out.println("Clock = "+clock+" processEndTime ="+processEndTime);
        return clock==processEndTime;
    }
    public DataBatch getCurrent_dataBatch(){

        //gets++;
        DataBatch d = current_dataBatch;
        current_dataBatch=null;
        return d;
    }

    public Type getType() {
        return type;
    }
    /**
     * Enum representing the type of the GPU.
     */




}
