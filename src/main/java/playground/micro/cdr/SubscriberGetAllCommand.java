package playground.micro.cdr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandMetrics;

public class SubscriberGetAllCommand extends HystrixCommand<long[]> {
	static final String QUERY = "/micro/sub/all";
	
	String subscriberUrlEndpoint;
	
	public SubscriberGetAllCommand(String subscriberUrlEndpoint, HystrixCommand.Setter config) {
		super(config);
		this.subscriberUrlEndpoint = subscriberUrlEndpoint;
	}

	@Override
	protected long[] run() throws Exception {
		URL url = new URL(subscriberUrlEndpoint+QUERY);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer content = new StringBuffer();
		String line;
		while ((line = in.readLine()) != null) {
			content.append(line);
		}
		in.close();
		long[] tacs = toLongs(content.toString());
		return tacs;
	}

	@Override
	public long[] getFallback() {
		System.out.println(SubscriberGetAllCommand.class.getName()+" Use fallback");
		Throwable t = getFailedExecutionException();
		t.printStackTrace();
		return new long[0];
	}
	
	
	public static long[] toLongs(String content) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Long> subscribers = objectMapper.readValue(content, List.class);
		long[] s = new long[subscribers.size()];
		for (int i=0; i<s.length; i++) {
			Object o = subscribers.get(i);
			Long l;
			//Its looks like the Jackson object mapper transform the list into integers and not longs
			if (o instanceof Integer) 
				l = new Long((Integer)o);
			else
				l = (Long)o;
			s[i] = l;
		}
		return s;
	}

}
