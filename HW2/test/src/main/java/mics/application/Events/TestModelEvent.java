package mics.application.Events;

import mics.Event;
import mics.Future;
import mics.application.objects.Model;
import mics.application.objects.Student;

public class TestModelEvent implements Event<Model> {
    private Future<Model> future;
    private Model model;
    private Student sender;
    public TestModelEvent(Model m, Student s){
        model=m;
        sender=s;
        future = null;
    }
    public Student getStudent(){
        return sender;
    }

    @Override
    public Model getData() {
        return model;
    }

    @Override
    public Future<Model> getFuture() {
        return future;
    }

    @Override
    public void SetFuture(Future<Model> f) {future = f;
    }

    @Override
    public String getSenderName() {
        return sender.getName();
    }
}
