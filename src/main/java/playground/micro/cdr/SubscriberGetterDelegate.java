package playground.micro.cdr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

//See https://github.com/Netflix/Hystrix/wiki/How-it-Works#flow-chart
public class SubscriberGetterDelegate {
	private static final boolean USE_HYSTRIX = true;
	String subscriberUrlEndpoint;
	int timeout;
	HystrixCommand.Setter config;
	
	public SubscriberGetterDelegate(String subscriberUrlEndpoint, int timeout) {
		this.subscriberUrlEndpoint = subscriberUrlEndpoint;
		this.timeout = timeout;
		
		config = HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SubscriberGetAllCommand.class.getName()));
		
		HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
		commandProperties.withExecutionTimeoutInMilliseconds(timeout);
		
		config.andCommandPropertiesDefaults(commandProperties);
		config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaxQueueSize(-1).withCoreSize(15));
	}
	
	public long[] getAllSubscribers() throws InterruptedException, ExecutionException, IOException {
		if (USE_HYSTRIX) return getAllSubscribersWithHystrix();
		else return getAllSubscribersWithNoHystrix();		
	}
	
	
	
	
	
	private long[] getAllSubscribersWithHystrix() throws InterruptedException, ExecutionException {
		SubscriberGetAllCommand command = new SubscriberGetAllCommand(subscriberUrlEndpoint, config);
//		return command.execute();
		Future<long[]> tacFuture = command.queue();
		return tacFuture.get();
	}
	
	private long[] getAllSubscribersWithNoHystrix() throws IOException {
		URL url = new URL(subscriberUrlEndpoint+SubscriberGetAllCommand.QUERY);
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
		long[] s = SubscriberGetAllCommand.toLongs(content.toString());
		return s;
	}
	
	
}
