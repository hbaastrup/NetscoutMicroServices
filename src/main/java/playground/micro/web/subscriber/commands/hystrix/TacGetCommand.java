package playground.micro.web.subscriber.commands.hystrix;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;
import playground.micro.models.TAC;
import playground.micro.web.subscriber.SubscriberTacCache;

public class TacGetCommand extends HystrixCommand<TAC> {
	public static final String QUERY = "/micro/tac/get/";
	String tacUrlEndpoint;
	int tac;

	public TacGetCommand(String tacUrlEndpoint, int tac, HystrixCommand.Setter config) {
		super(config);
		this.tacUrlEndpoint = tacUrlEndpoint;
		this.tac = tac;
	}

	@Override
	protected TAC run() throws Exception {
		URL url = new URL(tacUrlEndpoint+QUERY+tac);
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
		TAC value = objectMapper.readValue(content.toString(), TAC.class);
		SubscriberTacCache.INSTANCE.put(tac, value);
		return value;
	}
	
	@Override
	public TAC getFallback() {
		System.out.println(TacGetCommand.class.getName()+" Use fallback");
		Throwable t = getFailedExecutionException();
		t.printStackTrace();
		TAC value = SubscriberTacCache.INSTANCE.get(tac);
		return value;
	}

}
