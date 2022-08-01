package mics.application.services;

import mics.MessageBusImpl;
import mics.MessageBusSingleton;
import mics.MicroService;
import mics.application.Broadcasts.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int speed;
	private int duration;
	private int currentTime;
	private MessageBusImpl messageBus;

	public TimeService(String name, int speed, int duration) {
		super(name);
		// TODO Implement this
		this.speed=speed;
		this.currentTime=0;
		this.duration=duration;
		messageBus = MessageBusSingleton.getInstance();
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		messageBus.register(this);
		while(currentTime<=duration){
			sendBroadcast(new TickBroadcast(duration));
			if(currentTime%10==0)
			  System.out.println("Tick->"+currentTime);
			currentTime++;
			try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		terminate();
	}

}
