package playground.micro.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandMetrics;

public class CommandMetricsHolder {
	String commandName;
	int meanExecutionTime;
	long errorCount;
	long totalRequests;
	boolean circuitBreakerIsOpen = true;
	
	public CommandMetricsHolder(String name, int meanExecutionTime, long errorCount, long totalRequests, boolean circuitBreakerIsOpen) {
		this.commandName = name;
		this.meanExecutionTime = meanExecutionTime;
		this.errorCount = errorCount;
		this.totalRequests = totalRequests;
		this.circuitBreakerIsOpen = circuitBreakerIsOpen;
	}
	
	public String getName() {return commandName;}
	public int getMeanExecutionTime() {return meanExecutionTime;}
	public long getErrorCount() {return errorCount;}
	public long getTotalRequests() {return totalRequests;}
	public boolean isCircuitBreakerIsOpen() {return circuitBreakerIsOpen;}

	public static List<CommandMetricsHolder> instanceHystrixCommandMetricsList() {
		ArrayList<CommandMetricsHolder> retList = new ArrayList<>();
		Collection<HystrixCommandMetrics> hystrixMetrics = HystrixCommandMetrics.getInstances();
		for (HystrixCommandMetrics hm : hystrixMetrics) {
			HystrixCommandMetrics.HealthCounts hc = hm.getHealthCounts();
			HystrixCircuitBreaker breaker = HystrixCircuitBreaker.Factory.getInstance(hm.getCommandKey());
			CommandMetricsHolder holder = new CommandMetricsHolder(hm.getCommandKey().name(),
					hm.getExecutionTimeMean(),
					hc.getErrorCount(),
					hc.getTotalRequests(),
					breaker.isOpen());
			retList.add(holder);
		}
		return retList;

	}
}
