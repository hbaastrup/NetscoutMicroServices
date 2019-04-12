package playground.micro.orchestrating;

import java.util.HashMap;

public class EndpointHolder {
	String name;
	int port;
	String cmd;
	String url = "http://localhost";
	HashMap<String, String> args = new HashMap<>();
	String postArgs = "";
	Process process = null;

	public EndpointHolder(String name, int port, String command) {
		this.name = name;
		this.port = port;
		this.cmd = command;
		url += ":"+port;
	}
	
	public String getName() {return name;}
	
	public int getPort() {return port;}
	
	public String getCommand() {
		StringBuilder str = new StringBuilder(cmd+" ");
		for (String arg : args.keySet()) {
			String val = args.get(arg);
			str.append(arg+" "+val+" ");
		}
		str.append(postArgs);
		return str.toString();
	}
	
	public String[] getCommandAsArray() {
		String str = getCommand();
		return str.split("\\ ");
	}
	
	public String getUrl() {return url;}
	
	public void addArgument(String name, String value) {
		args.put(name, value);
	}
	
	public void setPostArgs(String args) {postArgs = args;}
	
	public void setProcess(Process p) {process = p;}
	public Process getProcess() {return process;}
}
