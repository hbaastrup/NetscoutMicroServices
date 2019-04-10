package playground.micro.producers.delegates;

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

import playground.micro.models.CDR;
import playground.micro.models.Subscriber;

//See https://github.com/Netflix/Hystrix/wiki/How-it-Works#flow-chart
public class SubscriberApiDelegate {
	private static final boolean USE_HYSTRIX = true;
	String subscriberUrlEndpoint;
	String cdrUrlEndpoint;
	int timeout;
	HystrixCommand.Setter config;
	
	public SubscriberApiDelegate(String subscriberUrlEndpoint, String cdrUrlEndpoint, int timeout) {
		this.subscriberUrlEndpoint = subscriberUrlEndpoint;
		this.cdrUrlEndpoint = cdrUrlEndpoint;
		this.timeout = timeout;
		
		config = HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SubscriberGetAllCommand.class.getName()));
		
		HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
		commandProperties.withExecutionTimeoutInMilliseconds(timeout);
		
		config.andCommandPropertiesDefaults(commandProperties);
		config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaxQueueSize(-1).withCoreSize(15));
	}
	
	
	public long[] getAllSubscribers() {
		try {
			if (USE_HYSTRIX) return getAllSubscribersWithHystrix();
			else return getAllSubscribersWithNoHystrix();	
		} catch (InterruptedException | ExecutionException | IOException e) {
			e.printStackTrace();
			return new long[0];
		}
	}
	
	public Long postTime(long subscriber, int time) {
		try {
			if (USE_HYSTRIX) return postTimeWithHystrix(subscriber, time);
			else return postTimeWithNoHystrix(subscriber, time);		
		} catch (InterruptedException | ExecutionException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean putCdr(CDR cdr) {
		try {
			if (USE_HYSTRIX) return putCdrWithHystrix(cdr);
			else return putCdrWithNoHystrix(cdr);
		} catch (InterruptedException | ExecutionException | IOException e) {
			e.printStackTrace();
			return false;
		}
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
	
	private Long postTimeWithHystrix(long subscriber, int time) throws InterruptedException, ExecutionException {
		SubscriberPostTimeCommand command = new SubscriberPostTimeCommand(subscriberUrlEndpoint, subscriber, time, config);
//		return command.execute();
		Future<Long> subscriberFuture = command.queue();
		return subscriberFuture.get();
	}
	
	private Long postTimeWithNoHystrix(long subscriber, int time) throws IOException {
		URL url = new URL(subscriberUrlEndpoint+SubscriberGetAllCommand.QUERY+"/"+subscriber+"/"+time);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("POST");
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
		Long subs = objectMapper.readValue(content.toString(), Long.class);
		return subs;
	}
	
	private boolean putCdrWithHystrix(CDR cdr) throws InterruptedException, ExecutionException {
		CdrPutCdrCommand command = new CdrPutCdrCommand(cdrUrlEndpoint, cdr, config);
//		return command.execute();
		Future<Boolean> subscriberFuture = command.queue();
		return subscriberFuture.get();
	}
	
	private boolean putCdrWithNoHystrix(CDR cdr) throws IOException {
		URL url = new URL(cdrUrlEndpoint+CdrPutCdrCommand.QUERY+"?calling="+cdr.getCalling()+"&called="+cdr.getCalled()+"&endtime="+cdr.getEndTime()+"&endtime="+cdr.getCalledTime());
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("PUT");
		con.setConnectTimeout(timeout);
		con.setReadTimeout(timeout);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer content = new StringBuffer();
		String line;
		while ((line = in.readLine()) != null) {
			content.append(line);
		}
		in.close();
		return true;
	}
	
	
}
