package playground.micro.producers.commands.hystrix;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.netflix.hystrix.HystrixCommand;

import playground.micro.models.CDR;

public class CdrPutCdrCommand extends HystrixCommand<Boolean> {
	public static final String QUERY  = "/micro/cdr/put";
	
	String cdrUrlEndpoint;
	CDR cdr;

	public CdrPutCdrCommand(String cdrUrlEndpoint, CDR cdr, HystrixCommand.Setter config) {
		super(config);
		this.cdrUrlEndpoint = cdrUrlEndpoint;
		this.cdr = cdr;
	}
	
	@Override
	protected Boolean run() throws Exception {
		URL url = new URL(cdrUrlEndpoint+QUERY+"?calling="+cdr.getCalling()+"&called="+cdr.getCalled()+"&endtime="+cdr.getEndTime()+"&duration="+cdr.getCalledTime());
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("PUT");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		StringBuffer content = new StringBuffer();
		while ((line = in.readLine()) != null) {
			content.append(line);
		}
		in.close();
		return true;
	}

	@Override
	public Boolean getFallback() {
		System.out.println(CdrPutCdrCommand.class.getName()+" Use fallback");
		Throwable t = getFailedExecutionException();
		t.printStackTrace();
		return false;
	}
}
