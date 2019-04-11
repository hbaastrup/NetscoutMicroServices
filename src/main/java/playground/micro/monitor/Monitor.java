package playground.micro.monitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import playground.micro.models.EnpointStatus;
import playground.micro.models.MonitorMetric;
import playground.micro.web.monitor.WebMonitor;

public class Monitor implements Runnable {
	ArrayList<EnpointStatus> endpointsStatus = new ArrayList<>();
	MonitorDeligate monitorDeligate;
	int serviceTimeout = 1500;
	
	boolean running = false;
	Thread thread = null;
	long pollTime;


	public Monitor(String[] endpointUrls, long pollTime) {
		for (String url : endpointUrls) {
			endpointsStatus.add(new EnpointStatus(url));
		}
		
		monitorDeligate = new MonitorDeligate(serviceTimeout);
		this.pollTime = pollTime;
		startThread();
	}
	
	public List<EnpointStatus> getAllStatus() {return endpointsStatus;}
	
	public void startThread() {
		stopThread();
		thread = new Thread(this, "Monitor");
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
			try {
				Thread.sleep(pollTime);
			} catch (InterruptedException e) {
				running = false;
				break;
			}
			
			queryAll();
		}
		running = false;
	}
	
	
	
	
	private void queryAll() {
		for (EnpointStatus status : endpointsStatus) {
			query(status);
		}
	}
	
	private void query(EnpointStatus status) {
		try {
			MonitorMetric metric = monitorDeligate.getMetric(status.getEndpointUrl());
			status.setMetric(metric);
		} catch (InterruptedException | ExecutionException | IOException e) {
			status.setMetric(null);
			e.printStackTrace();
		}
		status.setLastQuerytime(System.currentTimeMillis());
	}
	
	
	
	
	public static void main(String[] args) {
		int port = 10090;
		long pollTime = 5000;
		String[] endpoints = {"http://localhost:10080","http://localhost:10081","http://localhost:10082","http://localhost:10083"};
		ArrayList<String> endPoints = new ArrayList<>();

		for (int i=0; i<args.length; i++) {
			if ("-p".equals(args[i])) {
				i++;
				if (i<args.length)
					port = Integer.parseInt(args[i]);
			}
			else if ("-t".equals(args[i])) {
				i++;
				if (i<args.length)
					pollTime = Long.parseLong(args[i]);
			}
			else {
				endPoints.add(args[i]);
			}
		}
		
		if (!endPoints.isEmpty()) {
			endpoints = endPoints.toArray(new String[0]);
		}

		
		Monitor monitor = new Monitor(endpoints, pollTime);
		WebMonitor web = new WebMonitor(port, monitor);
		System.out.println("Monitor is running");
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("STOPPING!!!");
				monitor.stopThread();
				web.close();
				System.out.println("STOPPED!!!");
			}
		});

	}
}
