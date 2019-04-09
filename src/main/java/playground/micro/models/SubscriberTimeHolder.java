package playground.micro.models;

public class SubscriberTimeHolder {
	long subscriber;
	int time;

	public SubscriberTimeHolder(long subscriber, int time) {
		this.subscriber = subscriber;
		this.time = time;
	}
	
	public long getSubscriber() {return subscriber;}
	public int getTime() {return time;}
}
