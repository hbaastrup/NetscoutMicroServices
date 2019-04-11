package playground.micro.models;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonGetter;

public class EnpointStatus {
	String endpointUrl;
	MonitorMetric metric;
	long lastQuerytime = 0;
	
	public EnpointStatus(String endpointUrl) {
		this.endpointUrl = endpointUrl;
		this.metric = null;
	}
	
	@JsonGetter("endpointUrl")
	public String getEndpointUrl() {return endpointUrl;}
	
	@JsonGetter("metric")
	public MonitorMetric getMetric() {return metric;}
	public void setMetric(MonitorMetric metric) {this.metric = metric;}
	
	@JsonGetter("lastQuerytime")
	public long getLastQuerytime() {return lastQuerytime;}
	public void setLastQuerytime(long time) {lastQuerytime= time;}
	
	@JsonGetter("monitorEndpointAlive")
	public boolean isMonitorEndpointAlive() {
		return metric!=null;
	}
	
	@JsonGetter("serviceAlive")
	public boolean isServiceAlive() {
		if (metric==null) return false;
		return metric.isAlive();
	}
	
	@JsonGetter("hasServiceRequestProblems")
	public boolean hasServiceRequestProblems() {
		if (metric==null) return true;
		
		boolean hasProblems = false;
		Collection<CommandMetricsHolder> commandMetrics = metric.getCommandMetrics();
		for (CommandMetricsHolder cm : commandMetrics) {
			if (cm.isCircuitBreakerIsOpen()) hasProblems = true;
			if (cm.getErrorCount() > 0) hasProblems = true;
		}
		return hasProblems;
	}
	
	@JsonGetter("hasServiceSlowRequests")
	public boolean isServiceSlow() {
		if (metric==null) return true;
		
		boolean hasProblems = false;
		Collection<CommandMetricsHolder> commandMetrics = metric.getCommandMetrics();
		for (CommandMetricsHolder cm : commandMetrics) {
			if (cm.getMeanExecutionTime() > cm.getMaxExecutionTime()) hasProblems = true;
		}
		return hasProblems;
		
	}
}
