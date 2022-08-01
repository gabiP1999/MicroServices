package mics.application.services;

import mics.*;
import mics.application.Broadcasts.TickBroadcast;
import mics.application.Events.DataPreProcessEvent;
import mics.application.Events.DataProcessedEvent;
import mics.application.Events.TestModelEvent;
import mics.application.Events.TrainModelEvent;
import mics.application.objects.*;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link /}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU GPU;
    private final MessageBusImpl messageBus;
    private Model current_model;
    private Event<Model> current_event;
    private Cluster cluster;
    private DataBatch[] Unprocessed;
    private DataBatch[] processed;
    private DataBatch[] trained;
    private int unprocced_index;
    private int trained_index;
    private int processed_index;
    private int time;
    private int test_trained;

    public GPUService(String name, GPU.Type type) {
        super(name);
        // TODO Implement this
        this.messageBus = MessageBusSingleton.getInstance();
        this.cluster= Cluster.getInstance();
        GPU = new GPU(type);
        trained_index=0;
        unprocced_index=0;
        processed_index=-1;
        Unprocessed = new DataBatch[8];
        processed= new DataBatch[8];
        trained = new DataBatch[8];
        time=0;
        test_trained++;
    }
    private DataBatch[] ToDataBatches(Data data) {
        int size = (data.getSize()/1000);
        DataBatch[] res = new DataBatch[size];
        for(int i=0;i<size;i++){
            res[i] = new DataBatch(data,i*1000);
        }
        return res;
    }

    public int getTimeUse(){
        return GPU.getTimeUse();
    }

    @Override
    protected void initialize() {
        // TODO Implement this

        cluster.registerGPU(this);
        messageBus.register(this);
        subscribeBroadcast(TickBroadcast.class,b->{
            tick();
            if(time>=b.getDuration())terminate();
        });

        subscribeEvent(TestModelEvent.class,ev->{
            double rand = Math.random();
            if(ev.getStudent().getStatus()== Student.Degree.MSc){
                if(rand<0.6)ev.getData().setGood();
                else ev.getData().setBad();
            }
            else{
                if(rand<0.8)ev.getData().setGood();
                else ev.getData().setBad();
            }
            ev.getData().advanceStatus();
            complete(ev,ev.getData());
        });
        subscribeEvent(TrainModelEvent.class,ev->{
            if(current_event==null){
                setTrainingModel(ev);
                //System.out.println(this.getName()+" is Handling "+ev.getSenderName()+"'s TrainModel event");
            }
            else messageBus.sendEvent(ev);
        });
    }



    private void setTrainingModel(TrainModelEvent ev) {
        test_trained++;
        System.out.println(this.getName()+" Training "+ test_trained);

        //System.out.println(this.getName()+" setTraningModel");
        current_event=ev;
        current_model=ev.getData();
        current_model.advanceStatus();

        Unprocessed= ToDataBatches(current_model.getData());
        int size = getMaxVRAM();
        processed = new DataBatch[size];

        trained = new DataBatch[Unprocessed.length];
        trained_index=0;
        processed_index = -1;
        unprocced_index=0;
    }

    private int getMaxVRAM() {
        if(GPU.getType()== mics.application.objects.GPU.Type.GTX1080)return 8;
        if(GPU.getType()== mics.application.objects.GPU.Type.RTX2080)return 16;
        return 32;
    }

    public void tick(){

        time++;
        if(current_event==null){
            return;
        }
        //System.out.println(this.getName()+" Completed "+trained_index+"/"+trained.length+ " processed_index "+processed_index);
        GPU.tick();
        if(GPU.isReady()){
            addToTrained(GPU.getCurrent_dataBatch());
        }
        if(GPU.isNull()){
            processNext();
        }
        if((trained_index== Unprocessed.length-1)&(current_event!=null)){
            current_model.advanceStatus();
            complete(current_event,current_model);
            current_model=null;
            current_event=null;
        }
        sendDataPreProcess(NumberOfBatchesToSend());
        getProcessedDataBatches();
    }
    private void getProcessedDataBatches(){
        while(processed_index<(processed.length-1)){
            DataProcessedEvent e = cluster.awaitMessage(this);
            if(e==null){
                return;
            }
            DataBatch processedBatch = e.getDataBatch();
            processed[processed_index+1]=processedBatch;
            processed_index++;
        }
    }
    private int NumberOfBatchesToSend(){
        if(GPU.getType()== mics.application.objects.GPU.Type.GTX1080)return 1;
        if(GPU.getType()== mics.application.objects.GPU.Type.RTX2080)return 2;
        return 4;
    }
    private void sendDataPreProcess(int times){
        int i=0;
        while(unprocced_index<Unprocessed.length & i<times) {
            DataPreProcessEvent e = new DataPreProcessEvent(this,Unprocessed[unprocced_index]);
            cluster.sendEvent(e);
            unprocced_index++;
            i++;
        }
    }

    private void processNext() {
        if(processed_index!=-1){
            int ticks = getGPUTicks();
            if(processed[processed_index]==null) System.out.println("processed[processed_index] is null");
            GPU.setCurrent_dataBatch(processed[processed_index],ticks);
            processed[processed_index]=null;
            processed_index--;
        }
    }

    private int getGPUTicks() {
        if(GPU.getType()== mics.application.objects.GPU.Type.GTX1080)return 4;
        if(GPU.getType()== mics.application.objects.GPU.Type.RTX2080)return 2;
        return 1;
    }

    private void addToTrained(DataBatch current_dataBatch) {
        if(trained_index<trained.length){
            trained[trained_index]=current_dataBatch;
        }
        trained_index++;
    }
}
