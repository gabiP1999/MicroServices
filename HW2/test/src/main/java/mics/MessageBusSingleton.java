package mics;

public class MessageBusSingleton {
    private static MessageBusImpl instance;
    private MessageBusSingleton(){
        instance = null;
    }
    public static MessageBusImpl getInstance(){
        if(instance==null){
            instance=new MessageBusImpl();
        }
        return instance;
    }
}
