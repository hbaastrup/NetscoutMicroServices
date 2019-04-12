package playground.micro.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorMetric {
	String name = "";
	boolean alive = true;
	long timestamp = System.currentTimeMillis();
	HashMap<String, CommandMetricsHolder> commandMetrics = new HashMap<>();
	HashMap<String, Object> otherParameters = new HashMap<>();
	
	public String getName() {return name;}
	public MonitorMetric setName(String name) {this.name = name; return this;}
	public boolean isAlive() {return alive;}
	public long getTimestamp() {return timestamp;}
	
	public Collection<CommandMetricsHolder> getCommandMetrics() {
		return commandMetrics.values();
	}
	
	public MonitorMetric putParameter(String key, Object value) {
		otherParameters.put(key, value);
		return this;
	}
	
	public Map<String, Object> getOtherParameters() {return otherParameters;}
	
	public MonitorMetric setCommandMetrics(List<CommandMetricsHolder> list) {
		for (CommandMetricsHolder h : list) {
			commandMetrics.put(h.getName(), h);
		}
		return this;
	}
}
