package mics.application.services;

import mics.*;
import mics.application.Broadcasts.PublishConferenceBroadcast;
import mics.application.Broadcasts.TickBroadcast;
import mics.application.Events.PublishResultsEvent;
import mics.application.Events.TestModelEvent;
import mics.application.Events.TrainModelEvent;
import mics.application.objects.Model;
import mics.application.objects.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    private final MessageBusImpl messageBus;
    private Student student;
    private List<Model> toTrain;
    private Model current_model;
    private Future<Model> future_Trained;
    private Future<Model> future_Tested;
    private List<Model> trained_Models;
    private int time;
    private int train_events;
    private int test_events;




    public StudentService(String name,Student student) {
        super(name);
        // TODO Implement this
        trained_Models=new ArrayList<>();
        this.student=student;
        this.messageBus = MessageBusSingleton.getInstance();
        toTrain=student.getModels();
        current_model=null;
        future_Trained=null;
        future_Tested=null;
        time = 0;
        train_events=0;
        test_events=0;
    }
    public String getStudentName(){
        return student.getName();
    }
    public Student getStudent(){return student;}

    @Override
    protected void initialize() {
        // TODO Implement this
        messageBus.register(this);
        subscribeBroadcast(TickBroadcast.class,b->{
            tick();
            if(time>=b.getDuration())terminate();
        });
        subscribeBroadcast(PublishConferenceBroadcast.class,b->{
            for(Student s : b.getPapersPublishedByStudent().keySet()){
                if(s!=student){
                    int papers = b.getPapersPublishedByStudent().get(s);
                    student.addPapersRead(papers);
                }
            } });
    }

    private void tick() {
        //printState();
        time++;
        if((!hasNext())&(current_model==null))return;
        if((current_model==null)&(hasNext())){
            current_model=toTrain.remove(0);
        }
        if(current_model!=null){
            if(future_Tested==null & future_Trained==null){
                future_Trained=sendEvent(new TrainModelEvent(current_model,student));
                train_events++;
            }
            else if((future_Trained!=null)&&future_Trained.isDone()&future_Tested==null){
                future_Tested=sendEvent(new TestModelEvent(future_Trained.get(),student));
                test_events++;
            }
            else if((future_Trained!=null&future_Tested!=null)&&future_Trained.isDone()& future_Tested.isDone()){
                sendEvent(new PublishResultsEvent(future_Tested.get(),student));
                trained_Models.add(future_Tested.get());
                student.publish();
                current_model=null;
                future_Trained=null;
                future_Tested=null;
            }
        }
    }
    public List<Model> getOutputModels(){return trained_Models;}

    private void printState() {
        System.out.println("#################\n"+this.getName()+" Events:\n"+"Publish:"+getPublished()+" Tested:"+test_events+" Trained:"+train_events);
        for(Model m : student.getModels()){
            //System.out.println("Model "+m.getName()+" Size->"+m.getData().getSize()+" Status->"+m.getStatus()+" Results->"+m.getResults());
        }
        System.out.println("#################");
        if(future_Trained==null|| !future_Trained.isDone())return;
        System.out.println(this.getName()+" Future_Train->"+future_Trained.get().getStatus());
        if(future_Tested==null|| !future_Tested.isDone())return;
        System.out.println(this.getName()+" Future_Test->"+future_Tested.get().getStatus()+" res->"+future_Tested.get().getResults());

    }

    private boolean hasNext(){
        if(toTrain.isEmpty())return false;
        return true;
    }
    public int getPublished(){return student.getPublications();}
    public int getTrain_events(){return train_events;}

    public int getTest_events() {
        return test_events;
    }
}
