package mics.application.services;

import mics.*;
import mics.application.Broadcasts.TickBroadcast;
import mics.application.Events.DataPreProcessEvent;
import mics.application.Events.DataProcessedEvent;
import mics.application.objects.CPU;
import mics.application.objects.Cluster;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private CPU CPU;
    private DataPreProcessEvent current_event;
    private MessageBusImpl messageBus;
    private Cluster cluster;
    private int time;


    public CPUService(String name,  int cores) {
        super(name);
        // TODO Implement this
        CPU = new CPU(cores);
        this.messageBus= MessageBusSingleton.getInstance();
        cluster= Cluster.getInstance();
        current_event=null;
        time=0;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        messageBus.register(this);
        cluster.registerCPU(this);
        subscribeBroadcast(TickBroadcast.class,ev->{
            tick();
            if(time>=ev.getDuration())terminate();
        });
    }

    public void tick() {
        time++;
        CPU.tick();
        if(CPU.isReady()){
            DataProcessedEvent e = new DataProcessedEvent(CPU.getCurrent_dataBatch(),current_event.getSender());
            cluster.sendEvent(e);
            current_event=null;
        }
        getDataToProcess();
    }

    private void getDataToProcess() {
        if(current_event!=null)return;
        DataPreProcessEvent e = cluster.awaitMessage(this);
        if(e!=null){
            current_event=e;
            CPU.setCurrent_dataBatch(e.getDataBatch());
            //System.out.println(this.getName()+" setCurrent_dataBtach()");
        }
    }

    public int getCores(){
        return CPU.getCores();
    }
    public int getTimeUsed(){
        return CPU.getCpuTimeUse();
    }
    public int getBatchesProcessed(){
        return CPU.getDataBatchCount();
    }
}
