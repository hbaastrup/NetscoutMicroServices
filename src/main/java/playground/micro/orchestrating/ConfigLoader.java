package playground.micro.orchestrating;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigLoader {
	public Configuration load(File configFile) throws IOException {
		StringBuilder str = new StringBuilder();
		
		try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
			String line;
			while ((line = br.readLine())!=null) {
				str.append(line);
			}
		}
		
		ObjectMapper mapper = new ObjectMapper();
//		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Configuration configuration = mapper.readValue(str.toString(), Configuration.class);
		return configuration;
	}

	public static void main(String[] args) throws Exception {
		ConfigLoader configLoader = new ConfigLoader();
		Configuration configuration = configLoader.load(new File("config.json"));
		WorkerHolder worker = configuration.getWorker("CDR Producer");
		String arg = worker.getArgument("port");
	}
}
