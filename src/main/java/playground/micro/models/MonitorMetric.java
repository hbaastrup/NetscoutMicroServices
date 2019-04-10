package playground.micro.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MonitorMetric {
	boolean alive = true;
	long timestamp = System.currentTimeMillis();
	HashMap<String, CommandMetricsHolder> commandMetrics = new HashMap<>();
	
	public boolean isAlive() {return alive;}
	public long getTimestamp() {return timestamp;}
	
	public Collection<CommandMetricsHolder> getCommandMetrics() {
		return commandMetrics.values();
	}
	public MonitorMetric setCommandMetrics(List<CommandMetricsHolder> list) {
		for (CommandMetricsHolder h : list) {
			commandMetrics.put(h.getName(), h);
		}
		return this;
	}
}
