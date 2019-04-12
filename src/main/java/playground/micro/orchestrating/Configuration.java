package playground.micro.orchestrating;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {
	String jarFile;
	WorkerHolder[] workers;
	
	public String getJarFile() {return jarFile;}
	public WorkerHolder[] getWorkers() {return workers;}
	
	public WorkerHolder getWorker(String name) {
		for (WorkerHolder w : workers) {
			if (name.equals(w.getName()))
				return w;
		}
		return null;
	}
}
