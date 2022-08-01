package mics;

import java.util.*;

public class SafeBusDataBase {
    private Map<MicroService, Queue<Message>> registered;
    private Map<String,List<MicroService>> subscribedByEventType;
    private Map<String,List<MicroService>> subscribedByBroadcastType;
    private Map<String,Integer> roundRobin;
    private List<Event> completed;
    private List<Event> eventsSent;
    private List<Broadcast> broadcastsSent;

    public SafeBusDataBase(){
        registered=new HashMap<>();
        subscribedByEventType = new HashMap<>();
        subscribedByBroadcastType = new HashMap<>();
        completed = new ArrayList<>();
        roundRobin= new HashMap<>();
        eventsSent = new ArrayList<>();
        broadcastsSent = new ArrayList<>();
    }

    public synchronized void add(MicroService m){
       // System.out.println("SafeDataBase::add");
        registered.put(m,new ArrayDeque<Message>());
    }

    public synchronized void remove(MicroService m){
        removeFromRegistered(m);
        removeFromEvents(m);
        removeFromBroadcast(m);
    }


    private void removeFromRegistered(MicroService m){
        registered.remove(m);
    }

    private void removeFromEvents(MicroService m){
        for(List<MicroService> l : subscribedByEventType.values()){
            if(l.contains(m))l.remove(m);
        }
    }

    private void removeFromBroadcast(MicroService m){
        for(List<MicroService> l : subscribedByBroadcastType.values()){
            if(l.contains(m))l.remove(m);
        }
    }

    public synchronized void subscribeEvent(String event_type, MicroService m) {
        if(isRegistered(m)) {
            if (!eventExists(event_type))
                addNewEventType(event_type, m);
            else
                addMicroServiceToExistingEvent(event_type, m);
        }
    }
    public <T>boolean isSubscribedToEvent(Class<? extends Event<T>> type, MicroService m){
        if(!eventExists(type.getName()))return false;
        return subscribedByEventType.get(type.getName()).contains(m);
    }
    public <T> boolean isSubscribedToBroadcast(Class<? extends Broadcast> type, MicroService m){
        if(!broadcastExists(type.getName()))return false;
        return subscribedByBroadcastType.get(type.getName()).contains(m);
    }

    public synchronized boolean isRegistered(MicroService m) {
        return registered.containsKey(m);
    }

    private void addMicroServiceToExistingEvent(String event_type, MicroService m) {
        subscribedByEventType.get(event_type).add(m);
    }

    private boolean eventExists(String event_type) {
        return subscribedByEventType.containsKey(event_type);
    }

    private void addNewEventType(String event_type,MicroService m){
        List<MicroService> l = new ArrayList<>();
        l.add(m);
        subscribedByEventType.put(event_type,l);
        roundRobin.put(event_type,0);
    }

    public synchronized void subscribeBroadcast(String broadcast_type, MicroService m){
        if(isRegistered(m)) {
            if (!broadcastExists(broadcast_type))
                addNewBroadcast(broadcast_type, m);
            else
                addMicroServiceToExistingBroadcast(broadcast_type, m);
        }
    }

    private boolean broadcastExists(String broadcast_type) {
        return subscribedByBroadcastType.containsKey(broadcast_type);
    }

    private void addNewBroadcast(String b_type,MicroService m){
        ArrayList<MicroService> tmp_list = new ArrayList<>();
        tmp_list.add(m);
        subscribedByBroadcastType.put(b_type,tmp_list);
    }

    private void addMicroServiceToExistingBroadcast(String b_type, MicroService m) {
        subscribedByBroadcastType.get(b_type).add(m);
    }
    public synchronized Message awaitMessage(MicroService m) throws InterruptedException {
        while(!registered.containsKey(m)){
            System.out.println("awaitMessage from unregistered Microservice:"+m.getName());
            wait();
        }
        while (registered.get(m).isEmpty()) wait();
        return registered.get(m).poll();
    }

    public <T> void complete(Event<T> e, T result) {
        //System.out.println("MessageBus::Complete");
        completed.add(e);

        if(e==null) System.out.println("complete:: e is null");
        if(e.getFuture()==null) System.out.println("complete:: e.getFuture() is null "+e.getClass().getName());
        if(result==null) System.out.println("complete:: result is null");

        e.getFuture().resolve(result);
    }

    public <T> boolean isCompleted(Event<T> e) {
        return completed.contains(e);
    }

    public synchronized void sendBroadcast(Broadcast b) {
        String b_type = b.getClass().getName();
        if(broadcastExists(b_type)){
            if(subscribedByBroadcastType.get(b_type).size()>0){
                for(MicroService m : subscribedByBroadcastType.get(b_type)){
                    registered.get(m).add(b);
                }
            }
            broadcastsSent.add(b);
        }
        notifyAll();
    }

    public boolean wasBroadcastSent(Broadcast b) {
        return broadcastsSent.contains(b);
    }

    public synchronized  <T> Future<T> sendEvent(Event<T> e) {
        Future<T> future = new Future<>();
        if(e.getFuture()==null)e.SetFuture(future);
        String event_type = e.getClass().getName();
        if(eventExists(event_type) && (subscribedByEventType.get(event_type).size()!=0)){
            int index = roundRobin.get(event_type);
            if(index>=subscribedByEventType.get(event_type).size()){
                if(roundRobin.replace(event_type,index,0)) //System.out.println("round robin is back to 0");
                index=0;
            }
            MicroService m = subscribedByEventType.get(event_type).get(index);
            roundRobin.replace(event_type,index,index+1);
            //System.out.println("MessageBus:: adding Event to "+m.getName());
            registered.get(m).add(e);
            eventsSent.add(e);
        }
        notifyAll();
        return future;
    }

    public <T> boolean wasEventSent(Event<T> e) {
        return eventsSent.contains(e);
    }
}
