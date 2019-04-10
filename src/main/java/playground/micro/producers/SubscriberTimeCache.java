package playground.micro.producers;

import java.util.ArrayList;
import java.util.List;

import playground.micro.models.SubscriberTimeHolder;

public enum SubscriberTimeCache {
	INSTANCE;
	
	ArrayList<SubscriberTimeHolder> cache = new ArrayList<>();
	
	public synchronized void add(SubscriberTimeHolder holder) {
		cache.add(holder);
	}
	
	public synchronized List<SubscriberTimeHolder> extractAll() {
		ArrayList<SubscriberTimeHolder> list = new ArrayList<>();
		for (SubscriberTimeHolder sth : cache)
			list.add(sth);
		cache.clear();
		return list;
	}
	
	public int size() {
		return cache.size();
	}
}
