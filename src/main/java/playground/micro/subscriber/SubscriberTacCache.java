package playground.micro.subscriber;

import java.util.HashMap;

import playground.micro.tac.TAC;

public enum SubscriberTacCache {
	INSTANCE;
	
	HashMap<Integer, TAC> cache = new HashMap<>();
	
	public TAC get(int key) {
		return cache.get(key);
	}
	
	public void put(int key, TAC value) {
		cache.put(key, value);
	}

}
