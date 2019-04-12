package playground.micro.orchestrating;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

public class WorkerHolder {
	String name;
	String mainClass;
	int startNumber;
	JsonNode[] arguments;
	
	
	public String getName() {return name;}
	public String getMainClass() {return mainClass;}
	public int getStartNumber() {return startNumber;}
	public JsonNode[] getArguments() {return arguments;}
	
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
