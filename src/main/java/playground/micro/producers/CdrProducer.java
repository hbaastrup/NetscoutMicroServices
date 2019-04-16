package playground.micro.producers;

import java.util.List;
import java.util.Random;

import playground.micro.models.CDR;
import playground.micro.models.SubscriberTimeHolder;
import playground.micro.producers.delegates.SubscriberApiDelegate;
import playground.micro.web.cdr.producer.CdrProducerWeb;

public class CdrProducer implements Runnable {
	int serviceTimeout = 1500;
	String subscriberEndpoint;
	String cdrEndpoint;
	SubscriberApiDelegate apiDelegate;
	long[] subscribers = null;
	long lastRun = 0;
	
	Random rand = new Random();
	long standardSleepTime = 1000;
	long actualAverageSleepTime = standardSleepTime;
	int variationSleepTime = 200;
	long standardDurationTime = 120000;
	int variationDurationTime = 60000;
	int maxNumberOfCDRs = 10;
	int maxWebWaitRequestTime = 1000;
	
	int callFailures = 20; //in percent
	
	boolean running = false;
	Thread thread = null;

	public CdrProducer(String subscriberEndpoint, String cdrEndpoint) {
		this.subscriberEndpoint = subscriberEndpoint;
		this.cdrEndpoint = cdrEndpoint;
		
		apiDelegate = new SubscriberApiDelegate(subscriberEndpoint, cdrEndpoint, serviceTimeout);
		subscribers = apiDelegate.getAllSubscribers();
		
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
	
	public long getLastRun() {return lastRun;}
	public int getMaxWebWaitRequestTime() {return maxWebWaitRequestTime;}
	
	@Override
	public void run() {
		running = true;
		while (running) {
			lastRun = System.currentTimeMillis();
			long nextSleep = actualAverageSleepTime + variationSleepTime - rand.nextInt(2*variationSleepTime);
			try {
				Thread.sleep(nextSleep);
			} catch (InterruptedException e) {
				running = false;
				break;
			}
			
			if (subscribers==null || subscribers.length < 1) {
				subscribers = apiDelegate.getAllSubscribers();
				if (subscribers.length < 1)
					continue;
			}
			
			int numOfCdrs = maxNumberOfCDRs - rand.nextInt(maxNumberOfCDRs);
			boolean slowDown = false;
			for (int i=0; i<numOfCdrs; i++) {
				CDR cdr = createCDR();
				if (!apiDelegate.putCdr(cdr)) {
					slowDown = true;
					break;
				}
				
				Long newTime = apiDelegate.postTime(cdr.getCalling(), cdr.getCalledTime());
				if (newTime==null) {
					slowDown = true;
					break;
				}
			}
			
			if (slowDown) actualAverageSleepTime += standardSleepTime;
			else {
				actualAverageSleepTime = standardSleepTime;
				if (SubscriberTimeCache.INSTANCE.size() > 10) {
					System.out.println("Updateing times from cache");
					List<SubscriberTimeHolder> times = SubscriberTimeCache.INSTANCE.extractAll();
					for (SubscriberTimeHolder sth : times) {
						Long newTime= apiDelegate.postTime(sth.getSubscriber(), sth.getTime());
						if (newTime==null)
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
	
	
	
	public static void main(String[] args) {
		int port = 10084;
		String name = CdrProducerWeb.class.getName();
		String subscriberEndpoint = "http://localhost:10082";
		String cdrEndpoint = "http://localhost:10083";
		
		for (int i=0; i<args.length; i++) {
			if ("-p".equals(args[i])) {
				i++;
				if (i<args.length)
					port = Integer.parseInt(args[i]);
			}
			else if ("-n".equals(args[i])) {
				i++;
				if (i<args.length)
					name = args[i];
			}
			else if ("-s".equals(args[i])) {
				i++;
				if (i<args.length)
					subscriberEndpoint = args[i];
			}
			else if ("-c".equals(args[i])) {
				i++;
				if (i<args.length)
					cdrEndpoint = args[i];
			}
		}
		
		CdrProducer producer = new CdrProducer(subscriberEndpoint, cdrEndpoint);
		CdrProducerWeb web = new CdrProducerWeb(port, name, producer);
		System.out.println("CdrProducer is running");
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("STOPPING!!!");
				producer.stopThread();
				web.close();
				System.out.println("STOPPED!!!");
			}
		});
		
	}
}
