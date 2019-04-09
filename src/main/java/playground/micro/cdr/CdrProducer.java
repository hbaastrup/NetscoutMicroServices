package playground.micro.cdr;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import playground.micro.models.CDR;
import playground.micro.models.Subscriber;
import playground.micro.models.SubscriberTimeHolder;

public class CdrProducer implements Runnable {
	CdrDatabase database;
	int serviceTimeout = 1500;
	String subscriberEndpoint;
	SubscriberApiDelegate apiDelegate;
	long[] subscribers;
	
	Random rand = new Random();
	long standardSleepTime = 1000;
	long actualAverageSleepTime = standardSleepTime;
	int variationSleepTime = 200;
	long standardDurationTime = 120000;
	int variationDurationTime = 60000;
	int maxNumberOfCDRs = 10;
	
	int callFailures = 20; //in percent
	
	boolean running = false;
	Thread thread = null;

	public CdrProducer(String subscriberEndpoint, CdrDatabase database) {
		this.subscriberEndpoint = subscriberEndpoint;
		this.database = database;
		
		apiDelegate = new SubscriberApiDelegate(subscriberEndpoint, serviceTimeout);
		try {
			subscribers = apiDelegate.getAllSubscribers();
		} catch (InterruptedException | ExecutionException | IOException e) {
			e.printStackTrace();
			subscribers = new long[0];
			return;
		}
		
		if (subscribers.length < 1) {
			System.out.println("ERROR: No subscribers found");
			return;
		}
		
		startThread();
	}
	
	public void startThread() {
		stopThread();
		thread = new Thread(this, "CDR Producer");
		thread.start();
	}
	
	public void stopThread() {
		running = false;
		if (thread!=null)
			thread.interrupt();
		thread = null;
	}
	
	@Override
	public void run() {
		running = true;
		while (running) {
			long nextSleep = actualAverageSleepTime + variationSleepTime - rand.nextInt(2*variationSleepTime);
			try {
				Thread.sleep(nextSleep);
			} catch (InterruptedException e) {
				running = false;
				break;
			}
			
			int numOfCdrs = maxNumberOfCDRs - rand.nextInt(maxNumberOfCDRs);
			boolean slowDown = false;
			for (int i=0; i<numOfCdrs; i++) {
				CDR cdr = createCDR();
				database.add(cdr);
				
				Subscriber subscriber;
				try {
					subscriber = apiDelegate.postTime(cdr.getCalling(), cdr.getCalledTime());
				} catch (InterruptedException | ExecutionException | IOException e) {
					subscriber = null;
					e.printStackTrace();
				}
				if (subscriber==null) {
					slowDown = true;
					break;
				}
			}
			
			if (slowDown) actualAverageSleepTime += standardSleepTime;
			else {
				actualAverageSleepTime = standardSleepTime;
				if (SubscriberTimeCache.INSTANCE.size() > 10) {
					List<SubscriberTimeHolder> times = SubscriberTimeCache.INSTANCE.extractAll();
					for (SubscriberTimeHolder sth : times) {
						Subscriber subscriber;
						try {
							subscriber = apiDelegate.postTime(sth.getSubscriber(), sth.getTime());
						} catch (InterruptedException | ExecutionException | IOException e) {
							subscriber = null;
							e.printStackTrace();
						}
						if (subscriber==null)
							SubscriberTimeCache.INSTANCE.add(sth);
					}
				}
			}
		}
		running = false;
	}
	
	private CDR createCDR() {
		long now = System.currentTimeMillis();
		int inx1 = rand.nextInt(subscribers.length);
		int inx2 = rand.nextInt(subscribers.length);
		while (inx1==inx2) inx2 = rand.nextInt(subscribers.length);
		long calling = subscribers[inx1];
		long called = subscribers[inx2];
		if (rand.nextInt(100) < callFailures)
			return new CDR(calling, called, now);
		long duration = standardDurationTime + variationDurationTime - rand.nextInt(2*variationDurationTime);
		return new CDR(calling, called, now-duration, now);
	}
}
