package playground.micro.web.subscriber;

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
import playground.micro.models.TAC;
import playground.micro.web.subscriber.commands.hystrix.TacGetAllCommand;
import playground.micro.web.subscriber.commands.hystrix.TacGetCommand;

//See https://github.com/Netflix/Hystrix/wiki/How-it-Works#flow-chart
public class TacGetterDelegate {
	private static final boolean USE_HYSTRIX = true;
	String tacUrlEndpoint;
	int timeout;
	int rollingMetricWindow = 60000; //in milliseconds
	HystrixCommand.Setter config;
	
	public TacGetterDelegate(String tacUrlEndpoint, int timeout) {
		this.tacUrlEndpoint = tacUrlEndpoint;
		this.timeout = timeout;
		
		config = HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(TacGetAllCommand.class.getName()));
		
		HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
		commandProperties.withExecutionTimeoutInMilliseconds(timeout);
		commandProperties.withMetricsRollingStatisticalWindowInMilliseconds(rollingMetricWindow);
		
		config.andCommandPropertiesDefaults(commandProperties);
		config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaxQueueSize(-1).withCoreSize(15));
		
	}

	public TAC getTac(int tacID) throws InterruptedException, ExecutionException, IOException {
		if (USE_HYSTRIX) return getTacWithHystrix(tacID);
		else return getTacWithNoHystrix(tacID);
	}
	
	public int[] getAllTacs() throws InterruptedException, ExecutionException, IOException {
		if (USE_HYSTRIX) return getAllTacsWithHystrix();
		else return getAllTacsWithNoHystrix();		
	}
	
	
	
	
	
	private int[] getAllTacsWithHystrix() throws InterruptedException, ExecutionException {
		TacGetAllCommand command = new TacGetAllCommand(tacUrlEndpoint, config);
//		return command.execute();
		Future<int[]> tacFuture = command.queue();
		return tacFuture.get();
	}
	
	private int[] getAllTacsWithNoHystrix() throws IOException {
		URL url = new URL(tacUrlEndpoint+TacGetAllCommand.QUERY);
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
		int[] tacs = TacGetAllCommand.toIntegers(content.toString());
		return tacs;
	}
	
	
	private TAC getTacWithHystrix(int tacID) throws InterruptedException, ExecutionException {
		TacGetCommand command = new TacGetCommand(tacUrlEndpoint, tacID, config);
		Future<TAC> tacFuture = command.queue();
		return tacFuture.get();
	}
	
	private TAC getTacWithNoHystrix(int tacID) throws IOException {
		URL url = new URL(tacUrlEndpoint+TacGetCommand.QUERY+tacID);
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
		TAC tac = objectMapper.readValue(content.toString(), TAC.class);
		return tac;
	}
}
