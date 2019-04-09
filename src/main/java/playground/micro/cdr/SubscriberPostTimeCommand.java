package playground.micro.cdr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

import playground.micro.models.Subscriber;

public class SubscriberPostTimeCommand extends HystrixCommand<Subscriber> {
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
	protected Subscriber run() throws Exception {
		URL url = new URL(subscriberUrlEndpoint+QUERY+"/"+subscriber+"/"+time);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		StringBuffer content = new StringBuffer();
		while ((line = in.readLine()) != null) {
			content.append(line);
		}
		in.close();

		ObjectMapper objectMapper = new ObjectMapper();
		Subscriber value = objectMapper.readValue(content.toString(), Subscriber.class);
		return value;

	}
	
	@Override
	public Subscriber getFallback() {
		//TODO cache values
		return null;
	}

}
