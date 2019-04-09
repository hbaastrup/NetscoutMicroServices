package playground.micro.cdr;

import java.util.ArrayList;
import java.util.List;

import playground.micro.models.CDR;

public class CdrBucket {
	ArrayList<CDR> cdrs = new ArrayList<>();
	
	public void add(CDR cdr) {
		cdrs.add(cdr);
	}
	
	public List<CDR> getAll() {
		return cdrs;
	}
	
	public void clear() {
		cdrs.clear();
	}
}
