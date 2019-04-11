package playground.micro.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import playground.micro.models.MonitorMetric;
import playground.micro.web.subscriber.commands.hystrix.TacGetAllCommand;

public class MonitorDeligate {
	private static final boolean USE_HYSTRIX = true;
	int timeout;
	HystrixCommand.Setter config;
	
	public MonitorDeligate(int timeout) {
		this.timeout = timeout;
		
		config = HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(TacGetAllCommand.class.getName()));
		
		HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
		commandProperties.withExecutionTimeoutInMilliseconds(timeout);
		
		config.andCommandPropertiesDefaults(commandProperties);
		config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaxQueueSize(-1).withCoreSize(15));
	}
	
	public MonitorMetric getMetric(String endpointUrl) throws InterruptedException, ExecutionException, IOException {
		if (USE_HYSTRIX) return getMetricWithHystrix(endpointUrl);
		else return getMetricWithNoHystrix(endpointUrl);
	}
	
	private MonitorMetric getMetricWithHystrix(String endpointUrl) throws InterruptedException, ExecutionException{
		MonitorGetMetricCommand command = new MonitorGetMetricCommand(endpointUrl, config);
		Future<MonitorMetric> tacFuture = command.queue();
		return tacFuture.get();
	}
	
	private MonitorMetric getMetricWithNoHystrix(String endpointUrl) throws IOException {
		URL url = new URL(endpointUrl+MonitorGetMetricCommand.QUERY);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		con.setConnectTimeout(timeout);
		con.setReadTimeout(timeout);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer content = new StringBuffer();
		String line;
		while ((line = in.readLine()) != null) {
			content.append(line);
		}
		in.close();
		
		ObjectMapper objectMapper = new ObjectMapper();
		MonitorMetric monitorMetric = objectMapper.readValue(content.toString(), MonitorMetric.class);
		return monitorMetric;
	}

}
