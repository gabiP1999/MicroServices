package mics.application.services;

import mics.MessageBusImpl;
import mics.MessageBusSingleton;
import mics.MicroService;
import mics.application.Broadcasts.PublishConferenceBroadcast;
import mics.application.Broadcasts.TickBroadcast;
import mics.application.Events.PublishResultsEvent;
import mics.application.objects.ConfrenceInformation;
import mics.application.objects.Model;
import mics.application.objects.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private ConfrenceInformation confrence;
    private MessageBusImpl messageBus;
    private Map<Student, List<Model>> modelsByStudent;
    private  int time;

    public ConferenceService(String name,int date,String conf_name) {
        super(name);
        messageBus= MessageBusSingleton.getInstance();
        confrence=new ConfrenceInformation(conf_name,date);
        modelsByStudent = new HashMap<>();
        time =0;
        // TODO Implement this
    }

    public Map<Student, List<Model>> getModelsByStudent() {
        return modelsByStudent;
    }

    public String getConferenceName(){
        return confrence.getName();
    }
    public int getConferenceDate(){
        return confrence.getDate();
    }

    @Override
    protected void initialize() {
        messageBus.register(this);
        subscribeBroadcast(TickBroadcast.class,b->{
            tick();
            if(time>=b.getDuration())terminate();
        });
        subscribeEvent(PublishResultsEvent.class,ev->{
            System.out.println(this.getName()+" Model Status->"+ev.getData().getResults());
            if(ev.getData().getResults()!= Model.Results.Good)return;
            if(modelsByStudent.containsKey(ev.getStudent())){
                modelsByStudent.get(ev.getStudent()).add(ev.getData());
            }
            else{
                List<Model> newList = new ArrayList<>();
                newList.add(ev.getData());
                modelsByStudent.put(ev.getStudent(),newList);
            }
        });

        // TODO Implement this

    }

    public void tick() {
        time++;
        confrence.tick();
        if(confrence.isReady()){
            Map<Student,Integer> map = new HashMap<>();
            for(Student s : modelsByStudent.keySet()){
                int size = modelsByStudent.get(s).size();
                map.put(s,size);
            }
            System.out.println("###########");
            for (Student s : map.keySet()) System.out.println("<"+s.getName()+","+map.get(s)+">");
            System.out.println("###########");
            PublishConferenceBroadcast b = new PublishConferenceBroadcast(map);
            sendBroadcast(b);
            messageBus.unregister(this);
            terminate();
        }
    }


}
