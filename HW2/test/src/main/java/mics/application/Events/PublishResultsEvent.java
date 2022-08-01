package mics.application.Events;

import mics.Event;
import mics.Future;
import mics.application.objects.Model;
import mics.application.objects.Student;

public class PublishResultsEvent implements Event<Model> {

    Model model;
    Student student;
    public PublishResultsEvent(Model model, Student student){
        this.model = model;
        this.student = student;
    }
    @Override
    public Model getData() {
        return model;
    }

    @Override
    public Future<Model> getFuture() {
        return null;
    }

    @Override
    public void SetFuture(Future<Model> f) {

    }

    @Override
    public String getSenderName() {
        return null;
    }
    public Student getStudent(){return student;}
}
