package mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    private Data data;
    private String name;
    private Status status;
    public enum Status{PreTrained,Trained,Training, Tested}
    private Results results;
    public enum Results{None,Good,Bad}
    public Model(Data data,String name){
        this.data=data;
        status=Status.PreTrained;
        results=Results.None;
        this.name=name;
    }
    public void setGood(){
        results=Results.Good;
    }
    public void setBad(){
        results = Results.Bad;
    }
    public Data getData(){return data;}
    public Status getStatus(){return status;}
    public Results getResults(){return results;}
    public String getName(){return name;}
    public void advanceStatus(){
        if(status==Status.PreTrained)status=Status.Training;
        else if(status==Status.Training)status=Status.Trained;
        else if(status==Status.Trained)status=Status.Tested;
    }
}
