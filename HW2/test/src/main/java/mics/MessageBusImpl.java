package mics;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private SafeBusDataBase dataBase;
	public MessageBusImpl(){
		dataBase=new SafeBusDataBase();
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub
		//System.out.println("MessageBus::subscribeEvent("+type.getName()+",MicroService "+m.getName()+")");
		String event_type = type.getName();
		dataBase.subscribeEvent(event_type,m);

	}

	@Override
	public <T> boolean isSubscribedToEvent(Class<? extends Event<T>> type, MicroService m) {
		return dataBase.isSubscribedToEvent(type,m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub
		String event_type = type.getTypeName();
		dataBase.subscribeBroadcast(event_type,m);
	}

	@Override
	public boolean isSubscribedToBroadcast(Class<? extends Broadcast> type, MicroService m) {
		return dataBase.isSubscribedToBroadcast(type,m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub
		dataBase.complete(e,result);

	}

	@Override
	public <T> boolean isCompleted(Event<T> e) {
		return dataBase.isCompleted(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub
		dataBase.sendBroadcast(b);
	}

	@Override
	public boolean wasBroadcastSent(Broadcast b) {
		return dataBase.wasBroadcastSent(b);
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return dataBase.sendEvent(e);
	}

	@Override
	public <T> boolean wasEventSent(Event<T> e) {
		return dataBase.wasEventSent(e);
	}

	@Override
	public synchronized void register(MicroService m) {
		// TODO Auto-generated method stub
		//System.out.println("MessageBus::register");
			dataBase.add(m);

	}

	@Override
	public synchronized void unregister(MicroService m) {
		// TODO Auto-generated method stub
			dataBase.remove(m);

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return dataBase.awaitMessage(m);
	}
	public boolean isRegistered(MicroService m){
		return dataBase.isRegistered(m);
	}


	

}
