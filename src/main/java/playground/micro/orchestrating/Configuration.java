package playground.micro.orchestrating;

import java.util.ArrayList;

import hba.tuples.Pair;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {
	String jarFile;
	WorkerHolder[] workers;
	Pair<Integer, Integer> portRange;
	ArrayList<Integer> ports = new ArrayList<>();
	
	public String getJarFile() {return jarFile;}
	public WorkerHolder[] getWorkers() {return workers;}
	public int[] getPortRange() {return new int[] {portRange.getValue0(),portRange.getValue1()};}
	public void setPortRange(int[] range) {
		int minPort = range[0];
		int maxPort = range[1];
		if (maxPort < minPort) {
			int d = minPort;
			minPort = maxPort;
			maxPort = d;
		}
		portRange = new Pair<Integer, Integer>(minPort, maxPort);
		ports.clear();
		for (int i=minPort; i<=maxPort; i++) {
			ports.add(i);
		}
	}
	
	public int getNextPort() {
		if (ports.size()==0) return 0;
		int p = ports.get(0);
		ports.remove(0);
		return p;
	}
	
	public void returnPort(int port) {
		if (ports.contains(port)) return;
		ports.add(port);
	}
	
	public WorkerHolder getWorker(String name) {
		for (WorkerHolder w : workers) {
			if (name.equals(w.getName()))
				return w;
		}
		return null;
	}
}
