package playground.micro.loadbalancer.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import playground.micro.loadbalancer.IServerPing;
import playground.micro.loadbalancer.Server;

public class BaseHttpGetPing implements IServerPing {
	String httpQuery;
	int timeout = 10000;
	
	public BaseHttpGetPing(String httpQuery) {
		this.httpQuery = httpQuery;
	}
	
	@Override
	public boolean isAlive(Server server) {
		String scheme = server.getScheme()==null ? "http" : server.getScheme();
		String urlStr = scheme+"://"+server.getHost()+":"+server.getHostPort();
		try {
			URL url = new URL(urlStr+httpQuery);
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
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
