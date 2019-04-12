package playground.micro.orchestrating;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class WorkerHolder {
	String name;
	String mainClass;
	int startNumber;
	JsonNode[] arguments;
	
	
	public String getName() {return name;}
	public void setName(String name) {this.name = name.replaceAll("\\ ", "_");}
	public String getMainClass() {return mainClass;}
	public int getStartNumber() {return startNumber;}
	public JsonNode[] getArguments() {return arguments;}
	
	public List<String> getArgumentNames() {
		ArrayList<String> args = new ArrayList<>();
		for (JsonNode node : arguments) {
			Iterator<String> iter = node.fieldNames();
			while (iter.hasNext()) {
				String fName = iter.next();
				args.add(fName);
			}
		}
		return args;
	}
	
	public String getArgument(String name) {
		for (JsonNode node : arguments) {
			Iterator<String> iter = node.fieldNames();
			while (iter.hasNext()) {
				String fName = iter.next();
				if (name.equals(fName)) {
					JsonNode n = node.get(fName);
					return n.asText();
				}
			}
		}
		return null;
	}
}
