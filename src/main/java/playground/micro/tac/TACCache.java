package playground.micro.tac;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import playground.micro.models.TAC;

public class TACCache {
	HashMap<Integer, TAC> cache = new HashMap<>();
	
	public TAC get(String tac) {
		return get(TAC.toInt(tac));
	}
	
	public TAC get(int tac) {
		TAC i = cache.get(tac);
		if (i==null) i = TAC.createNullObject();
		return i;
	}
	
	public List<Integer> getAllTacs() {
		ArrayList<Integer> list = new ArrayList<>(cache.keySet());
		return list;
	}
	
	public void load(File file) throws IOException {
		try (Scanner sc = new Scanner(file);) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] feilds = line.split(";");
				if (feilds.length < 4) continue;
				try {
					TAC tac = new TAC(feilds[0], feilds[1], feilds[2], feilds[3]);
					cache.put(TAC.toInt(feilds[0]), tac);
				} catch (NumberFormatException e) {
					continue;
				}
			}
		}
	}
}
