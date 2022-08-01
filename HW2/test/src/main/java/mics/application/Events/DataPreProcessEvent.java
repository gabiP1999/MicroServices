package mics.application.Events;

import mics.application.objects.Data;
import mics.application.objects.DataBatch;
import mics.application.services.GPUService;

public class DataPreProcessEvent {
    private GPUService sender;
    private DataBatch toProcess;

    public DataPreProcessEvent(GPUService sender, DataBatch toProcess){
        this.sender = sender;
        this.toProcess = toProcess;
    }
    public int getTypeMultiple(){
        if(toProcess==null){
            System.out.println("toProcess is null");
            return 2;
        }
        if(toProcess.getType()== Data.Type.Images)return 4;
        if(toProcess.getType()== Data.Type.Tabular)return 1;
        return 2;
    }
    public GPUService getSender(){return sender;}

    public DataBatch getDataBatch() {return toProcess;
    }
}
