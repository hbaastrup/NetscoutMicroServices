package playground.micro.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

import playground.micro.models.MonitorMetric;

public class MonitorGetMetricCommand extends HystrixCommand<MonitorMetric> {
	public final static String QUERY = "/micro/metic";
	
	private String endpointUrl;

	public MonitorGetMetricCommand(String endpointUrl, HystrixCommand.Setter config) {
		super(config);
		this.endpointUrl = endpointUrl;
	}
	
	@Override
	protected MonitorMetric run() throws Exception {
		URL url = new URL(endpointUrl+QUERY);
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
		MonitorMetric monitorMetric = objectMapper.readValue(content.toString(), MonitorMetric.class);
		return monitorMetric;
	}

	@Override
	public MonitorMetric getFallback() {
		System.out.println(MonitorMetric.class.getName()+" Use fallback");
		Throwable t = getFailedExecutionException();
		t.printStackTrace();
		return null;
	}
}
