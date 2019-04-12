package playground.micro.orchestrating;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class Orchestrator {
	Configuration configuration = null;

	public void bootstrap(String configFilename) throws IOException {
		File f = new File(configFilename);
		ConfigLoader loader = new ConfigLoader();
		configuration = loader.load(f);
		
		WorkerHolder[] workers = configuration.getWorkers();
		Arrays.sort(workers, new WorkerHolderComparator());
		
		for (WorkerHolder worker : workers) {
			StringBuilder cmd = new StringBuilder();
		}
	}
	
	
	class WorkerHolderComparator implements Comparator<WorkerHolder> {

		@Override
		public int compare(WorkerHolder o1, WorkerHolder o2) {
			if (o1.getStartNumber() > o2.getStartNumber()) return 1;
			else if (o1.getStartNumber() == o2.getStartNumber()) return 0;
			return -1;
		}
		
	}
}
