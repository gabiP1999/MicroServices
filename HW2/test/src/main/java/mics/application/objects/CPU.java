package mics.application.objects;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private int cores;
    private int clock;
    private DataBatch current_dataBatch;
    private int processEndTime;
    private int cpu_timeUse;
    private int dataBatch_cnt;

    public CPU (int cores){
        this.cores = cores;
        this.clock=0;
        this.cpu_timeUse=0;
        dataBatch_cnt=0;
    }
    public int getDataBatchCount(){return dataBatch_cnt;}
    public int getCpuTimeUse(){return cpu_timeUse;}

    public DataBatch getCurrent_dataBatch(){
        dataBatch_cnt++;
        DataBatch res = current_dataBatch;
        current_dataBatch=null;
        return res;
    }
    public void setCurrent_dataBatch(DataBatch current_dataBatch) {
        //System.out.println("cpu use time:"+cpu_timeUse+" databatch count:"+dataBatch_cnt);
        int ticks = calculateTime(current_dataBatch);
        processEndTime= clock + ticks;
        this.current_dataBatch=current_dataBatch;

    }
    public boolean isReady(){
        if(current_dataBatch==null)return false;
        return clock>=processEndTime;
    }

    public void tick(){
        clock++;
        if(current_dataBatch!=null){
            cpu_timeUse++;
        }
    }

    private int calculateTime(DataBatch dataBatch){
        if(dataBatch.getType()== Data.Type.Images)
            return (32/cores)*4;
        if(dataBatch.getType()== Data.Type.Text)
            return (32/cores)*2;
        if(dataBatch.getType()== Data.Type.Tabular)
            return (32/cores);
        return 0;
    }
    public int getCores(){
        return cores;
    }


    public int getClock() {
        return clock;
    }
}
