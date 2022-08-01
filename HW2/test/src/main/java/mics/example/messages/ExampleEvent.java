package mics.example.messages;

import mics.Event;
import mics.Future;

public class ExampleEvent implements Event<String>{

    private String senderName;

    public ExampleEvent(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    @Override
    public String getData() {
        return null;
    }

    @Override
    public Future<String> getFuture() {
        return null;
    }

    @Override
    public void SetFuture(Future<String> f) {

    }
}