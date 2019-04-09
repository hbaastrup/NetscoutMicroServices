package playground.micro.cdr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

import playground.micro.models.Subscriber;
import playground.micro.models.SubscriberTimeHolder;
import playground.micro.subscriber.TacGetCommand;

public class SubscriberPostTimeCommand extends HystrixCommand<Long> {
	static final String QUERY = "/micro/sub/time";
	
	String subscriberUrlEndpoint;
	long subscriber;
	int time;
	
	public SubscriberPostTimeCommand(String subscriberUrlEndpoint, long subscriber, int time, HystrixCommand.Setter config) {
		super(config);
		this.subscriberUrlEndpoint = subscriberUrlEndpoint;
		this.subscriber = subscriber;
		this.time = time;
	}
	
	@Override
	protected Long run() throws Exception {
		URL url = new URL(subscriberUrlEndpoint+QUERY+"/"+subscriber+"/"+time);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("POST");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		StringBuffer content = new StringBuffer();
		while ((line = in.readLine()) != null) {
			content.append(line);
		}
		in.close();

		ObjectMapper objectMapper = new ObjectMapper();
		Long value = objectMapper.readValue(content.toString(), Long.class);
		return value;

	}
	
	@Override
	public Long getFallback() {
		System.out.println(SubscriberPostTimeCommand.class.getName()+" Use fallback");
		Throwable t = getFailedExecutionException();
		t.printStackTrace();

		SubscriberTimeCache.INSTANCE.add(new SubscriberTimeHolder(subscriber, time));
		return null;
	}

}
