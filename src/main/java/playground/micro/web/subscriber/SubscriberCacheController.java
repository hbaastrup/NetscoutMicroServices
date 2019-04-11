package playground.micro.web.subscriber;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.micro.models.Subscriber;

public class SubscriberCacheController {
	
	SubscriberCache cache;
	String tacServiceUrl;
	int timeout = 1000;
	TacGetterDelegate getter;
	
	ObjectMapper objectMapper = new ObjectMapper();

	public SubscriberCacheController(String tacServiceUrl) {
		this.tacServiceUrl = tacServiceUrl;
		
		getter = new TacGetterDelegate(tacServiceUrl, timeout);
		int[] tacs;
		try {
			tacs = getter.getAllTacs();
		} catch (InterruptedException | ExecutionException | IOException e) {
			e.printStackTrace();
			tacs = new int[]{0};
		}
		
		cache = new SubscriberCache();
		cache.startUp(tacs);
		System.out.println("Phone Cache started-up");
	}
	
	public int getTimeout() {return timeout;}
	
	public Subscriber getPhone(String number) {
		long n = Long.parseLong(number);
		return getPhone(n);
	}
	
	public Subscriber getPhone(long number) {
		Subscriber p = cache.get(number);
		if (p!=null) {
			p.lasyLoad(getter);
		}
		return p;
	}
	
	public List<Long> getAllPhones() {
		return cache.getAllPhones();
	}
}
