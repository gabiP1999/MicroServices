package mics.application.Events;

import mics.Future;
import mics.application.objects.Model;
import mics.Event;
import mics.application.objects.Student;

public class TrainModelEvent implements Event<Model> {
    private Model model ;
    private Future<Model> future;
    private Student sender;

    public TrainModelEvent(Model model,Student sender){
        this.model = model;
        this.sender=sender;
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
    public void SetFuture(Future<Model> f) {
        future=f;
    }

    @Override
    public String getSenderName() {
        return sender.getName();
    }
}
