package playground.micro.kpi;

public class KpiBase {
	
	final long timestamp; // EPOC in milliseconds
	
	public KpiBase() {
		timestamp = System.currentTimeMillis();
	}

	public long getTimestamp() {return timestamp;}
}
