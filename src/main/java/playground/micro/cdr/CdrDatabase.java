package playground.micro.cdr;

import java.util.ArrayList;
import java.util.List;

import hba.tuples.Pair;

public class CdrDatabase {
	static final int MAX_TIME_SLOTS = 0x20000; //in seconds (86400=24H => multiple with 2 = 20000(hex))
	static final int MASK = MAX_TIME_SLOTS-1;
	
	CdrBucket[] buckets = new CdrBucket[MAX_TIME_SLOTS];
	int tail = Integer.MIN_VALUE;
	int head = Integer.MIN_VALUE;
	
	public CdrDatabase() {
		for (int i=0; i<buckets.length; i++)
			buckets[i] = new CdrBucket();
	}
	
	public void add(CDR cdr) {
		int endTimeSec = (int)(cdr.getEndTime()/1000);
		int i = (int)(endTimeSec & MASK);
		clean(i);
		buckets[i].add(cdr);
	}
	
	/**
	 * 
	 * @param fromTime in seconds
	 * @param toTime in seconds
	 * @return
	 */
	public List<CDR> get(int fromTime, int toTime) {
		ArrayList<CDR> list = new ArrayList<>();
		int fromInx = fromTime & MASK;
		int toInx = toTime & MASK;
		
		if (fromInx<=toInx) {
			for (int i=fromInx; i<=toInx; i++) {
				CdrBucket bucket = buckets[i];
				for (CDR cdr : bucket.getAll())
					list.add(cdr);
			}
		}
		else {
			for (int i=fromInx; i<MAX_TIME_SLOTS; i++) {
				CdrBucket bucket = buckets[i];
				for (CDR cdr : bucket.getAll())
					list.add(cdr);
			}
			for (int i=0; i<=toInx; i++) {
				CdrBucket bucket = buckets[i];
				for (CDR cdr : bucket.getAll())
					list.add(cdr);
			}
		}
		
		return list;
	}
	
	public List<CDR> get(String fromTime, String toTime) {
		return get(Integer.parseInt(fromTime), Integer.parseInt(toTime));
	}
	
	public Pair<Integer, Integer> getMinMaxTime() {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		
		CdrBucket bucket = buckets[tail];
		for (CDR cdr : bucket.getAll()) {
			int sec = (int)(cdr.getEndTime()/1000);
			if (sec < min) 
				min = sec;
		}
		
		bucket = buckets[head];
		for (CDR cdr : bucket.getAll()) {
			int sec = (int)(cdr.getBeginTime()/1000);
			if (sec > max) 
				max = sec;
		}
		return new Pair<Integer, Integer>(min, max);
	}
	
	
	
	
	private void clean(int inx) {
		//TODO
		if (tail < 0) tail = inx;
		if (inx > head || inx < tail) head = inx;
	}

}
