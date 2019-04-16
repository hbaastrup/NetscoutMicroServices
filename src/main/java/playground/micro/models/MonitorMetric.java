package playground.micro.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hba.gc.GcEvent;

public class MonitorMetric {
	String name = "";
	boolean alive = true;
	long timestamp = System.currentTimeMillis();
	HashMap<String, CommandMetricsHolder> commandMetrics = new HashMap<>();
	HashMap<String, Object> otherParameters = new HashMap<>();
	List<GcEvent> gcEvents = null;
	
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
	
	public List<GcEvent> getGcEvents() {return gcEvents;}
	public void setGcEvents(List<GcEvent> events) {gcEvents = events;}
	
	public MonitorMetric setCommandMetrics(List<CommandMetricsHolder> list) {
		for (CommandMetricsHolder h : list) {
			commandMetrics.put(h.getName(), h);
		}
		return this;
	}
}
