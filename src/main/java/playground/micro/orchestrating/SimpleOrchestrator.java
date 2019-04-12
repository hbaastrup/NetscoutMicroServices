package playground.micro.orchestrating;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import playground.micro.web.orchestrator.WebOrchestrator;
public class SimpleOrchestrator {
	int webPort = 0;
	String name = SimpleOrchestrator.class.getName();
	String javaCmd = "/opt/java/bin/java";
	Configuration configuration = null;
	ArrayList<EndpointHolder> endpoints = new ArrayList<>();
	
	public SimpleOrchestrator setJavaCommand(String cmd) {javaCmd = cmd; return this;}
	
	public int getWebPort() {return webPort;}
	
	public String getName() {return name;}

	public void bootstrap(String configFilename, boolean seperatedProcess) throws IOException {
		File f = new File(configFilename);
		ConfigLoader loader = new ConfigLoader();
		configuration = loader.load(f);
		
		WorkerHolder[] workers = configuration.getWorkers();
		Arrays.sort(workers, new WorkerHolderComparator());
		
		createEndpoints(workers);
		addArgs(endpoints);
		bootstrap(endpoints, seperatedProcess);
	}
	
	public void close() {
		for (EndpointHolder endpoint : endpoints) {
			if ("Orchestrator".equals(endpoint.getName())) continue;
			Process p = endpoint.getProcess();
			p.destroy();
		}
	}
	
	
	
	private void createEndpoints(WorkerHolder[] workers) throws IOException {
		for (WorkerHolder worker : workers) {
			int port = configuration.getNextPort();
			if (port==0) throw new IOException("The configuration is run-out of ports");
			
			EndpointHolder endpoint = new EndpointHolder(worker.getName(), port, javaCmd+" -cp "+configuration.jarFile+" "+worker.getMainClass());
			if ("Orchestrator".equals(endpoint.getName())) {
				webPort = port;
				name = endpoint.getName();
			}
			
			endpoints.add(endpoint);
		}
	}
	
	private void addArgs(List<EndpointHolder> endpoints) {
		for (EndpointHolder endpoint : endpoints) {
			WorkerHolder worker = configuration.getWorker(endpoint.getName());
			if (worker==null) continue; //TODO: maybe we should throw an exception 
			addArgs(worker, endpoint);
			if ("Monitor".equals(endpoint.getName())) {
				endpoint.setPostArgs(createMonitorEndpoints());
			}
		}
	}
	
	private void bootstrap(List<EndpointHolder> endpoints, boolean seperatedProcess) throws IOException {
		for (EndpointHolder endpoint : endpoints) {
			System.out.println(endpoint.getCommand());
			if ("Orchestrator".equals(endpoint.getName())) continue;
			if (seperatedProcess) {
				String[] cmdArr = endpoint.getCommandAsArray();
				ProcessBuilder pb = new ProcessBuilder(cmdArr);
				//pb.directory(new File("."));
				File log = new File(endpoint.getName().replaceAll("\\ ", "-")+".log");
				pb.redirectErrorStream(true);
				pb.redirectOutput(Redirect.appendTo(log));
				//Map<String, String> env = pb.environment();

				Process p = pb.start();
				endpoint.setProcess(p);
			}
		}
	}
	
	private void addArgs(WorkerHolder worker, EndpointHolder endpoint) {
		for (String argName : worker.getArgumentNames()) {
			String arg = worker.getArgument(argName);
			switch (argName) {
				case "port":
					endpoint.addArgument(arg, String.valueOf(endpoint.getPort()));
					break;
				case "name":
					endpoint.addArgument(arg, worker.getName());
					break;
				default:
					EndpointHolder ep = getEndpoint(argName);
					if (ep!=null) {
						endpoint.addArgument(arg, ep.getUrl());
					}
			}
		}
	}
	
	private String createMonitorEndpoints() {
		StringBuilder str = new StringBuilder();
		for (EndpointHolder ep : endpoints) {
			if (ep.getCommand().contains("Monitor")) continue;
			str.append(ep.getUrl()+" ");
		}
		return str.toString();
	}
	
	private EndpointHolder getEndpoint(String name) {
		for (EndpointHolder ep :endpoints) {
			if (name.equals(ep.getName())) return ep;
		}
		return null;
	}
	
	
	class WorkerHolderComparator implements Comparator<WorkerHolder> {

		@Override
		public int compare(WorkerHolder o1, WorkerHolder o2) {
			if (o1.getStartNumber() > o2.getStartNumber()) return 1;
			else if (o1.getStartNumber() == o2.getStartNumber()) return 0;
			return -1;
		}
		
	}
	
	
	
	public static void main(String[] args) throws Exception {
		int port = 10090;
		String name = SimpleOrchestrator.class.getName();
		String javaCmd = "/opt/java/bin/java";
		String configFilename = "config.json";
		
		boolean runInSeperatedProcesses = true;
		
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
			else if ("-j".equals(args[i])) {
				i++;
				if (i<args.length)
					javaCmd = args[i];
			}
		}

		
		SimpleOrchestrator orchestrator = new SimpleOrchestrator().setJavaCommand(javaCmd);
		orchestrator.bootstrap(configFilename, runInSeperatedProcesses);
		if (orchestrator.getWebPort()!=0) port = orchestrator.getWebPort();
		WebOrchestrator web = new WebOrchestrator(port, orchestrator.getName()); 
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				for (;;)
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
		t.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("STOPPING!!!");
				orchestrator.close();
				web.close();
				System.out.println("STOPPED!!!");
			}
		});
	}
}
