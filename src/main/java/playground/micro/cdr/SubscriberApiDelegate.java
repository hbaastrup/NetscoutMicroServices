package playground.micro.cdr;

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

import playground.micro.models.Subscriber;
import playground.micro.models.TAC;

//See https://github.com/Netflix/Hystrix/wiki/How-it-Works#flow-chart
public class SubscriberApiDelegate {
	private static final boolean USE_HYSTRIX = true;
	String subscriberUrlEndpoint;
	int timeout;
	HystrixCommand.Setter config;
	
	public SubscriberApiDelegate(String subscriberUrlEndpoint, int timeout) {
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
	
	public Subscriber postTime(long subscriber, int time) throws InterruptedException, ExecutionException, IOException {
		if (USE_HYSTRIX) return postTimeWithHystrix(subscriber, time);
		else return postTimeWithNoHystrix(subscriber, time);		
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
	
	private Subscriber postTimeWithHystrix(long subscriber, int time) throws InterruptedException, ExecutionException {
		SubscriberPostTimeCommand command = new SubscriberPostTimeCommand(subscriberUrlEndpoint, subscriber, time, config);
//		return command.execute();
		Future<Subscriber> subscriberFuture = command.queue();
		return subscriberFuture.get();
	}
	
	private Subscriber postTimeWithNoHystrix(long subscriber, int time) throws IOException {
		URL url = new URL(subscriberUrlEndpoint+SubscriberGetAllCommand.QUERY+"/"+subscriber+"/"+time);
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
		Subscriber subs = objectMapper.readValue(content.toString(), Subscriber.class);
		return subs;
	}
	
	
}
