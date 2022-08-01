package mics.application.Events;

import mics.application.objects.DataBatch;
import mics.application.services.GPUService;

public class DataProcessedEvent {
    private DataBatch dataBatch;
    private GPUService toSend;
    public DataProcessedEvent(DataBatch dataBatch, GPUService toSend){
        this.dataBatch = dataBatch;
        this.toSend = toSend;
    }

    public GPUService toSend() {
        return toSend;
    }

    public DataBatch getDataBatch() {
        return dataBatch;
    }
}
