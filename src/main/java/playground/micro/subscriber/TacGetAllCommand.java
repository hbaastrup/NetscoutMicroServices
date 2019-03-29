package playground.micro.subscriber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;
import playground.micro.tac.TACArray;

public class TacGetAllCommand extends HystrixCommand<int[]> {
	static final String QUERY = "/micro/tac/all";
	
	String tacUrlEndpoint;
	
	public TacGetAllCommand(String tacUrlEndpoint, HystrixCommand.Setter config) {
		super(config);
		this.tacUrlEndpoint = tacUrlEndpoint;
	}

	@Override
	protected int[] run() throws Exception {
		URL url = new URL(tacUrlEndpoint+QUERY);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer content = new StringBuffer();
		String line;
		while ((line = in.readLine()) != null) {
			content.append(line);
		}
		in.close();
		int[] tacs = toIntegers(content.toString());
		return tacs;
	}

	@Override
	public int[] getFallback() {
		System.out.println(TacGetAllCommand.class.getName()+" Use fallback");
		Throwable t = getFailedExecutionException();
		t.printStackTrace();
		return new int[]{0};
	}
	
	
	public static int[] toIntegers(String content) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		TACArray tacs = objectMapper.readValue(content, TACArray.class);
		return tacs.getTacs();
	}

}
