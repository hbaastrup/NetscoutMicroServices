package playground.micro.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandMetrics;

public class CommandMetricsHolder {
	String commandName;
	int meanExecutionTime;
	int maxExecutionTime;
	long errorCount;
	long totalRequests;
	boolean circuitBreakerIsOpen = true;
	
	protected CommandMetricsHolder() {}
	
	public CommandMetricsHolder(String name, int meanExecutionTime, int maxExecutionTime, long errorCount, long totalRequests, boolean circuitBreakerIsOpen) {
		this.commandName = name;
		this.meanExecutionTime = meanExecutionTime;
		this.maxExecutionTime = maxExecutionTime;
		this.errorCount = errorCount;
		this.totalRequests = totalRequests;
		this.circuitBreakerIsOpen = circuitBreakerIsOpen;
	}
	
	public String getName() {return commandName;}
	protected void setName(String name) {this.commandName = name;}
	public int getMeanExecutionTime() {return meanExecutionTime;}
	public int getMaxExecutionTime() {return maxExecutionTime;}
	public long getErrorCount() {return errorCount;}
	public long getTotalRequests() {return totalRequests;}
	public boolean isCircuitBreakerIsOpen() {return circuitBreakerIsOpen;}

	public static List<CommandMetricsHolder> instanceHystrixCommandMetricsList(int maxExecutionTime) {
		ArrayList<CommandMetricsHolder> retList = new ArrayList<>();
		Collection<HystrixCommandMetrics> hystrixMetrics = HystrixCommandMetrics.getInstances();
		for (HystrixCommandMetrics hm : hystrixMetrics) {
			HystrixCommandMetrics.HealthCounts hc = hm.getHealthCounts();
			HystrixCircuitBreaker breaker = HystrixCircuitBreaker.Factory.getInstance(hm.getCommandKey());
			CommandMetricsHolder holder = new CommandMetricsHolder(hm.getCommandKey().name(),
					hm.getExecutionTimeMean(),
					maxExecutionTime,
					hc.getErrorCount(),
					hc.getTotalRequests(),
					breaker.isOpen());
			retList.add(holder);
		}
		return retList;

	}
}
