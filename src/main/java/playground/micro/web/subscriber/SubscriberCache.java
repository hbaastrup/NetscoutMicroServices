package playground.micro.web.subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import playground.micro.models.Subscriber;

public class SubscriberCache {
	static final int NUMBER_OF_PHONES = 1000;
	static final int NUMBER_OF_DIGITS_IN_NUMBER = 8;
	
	HashMap<Long, Subscriber> cache = new HashMap<>();
	
	int[] numbers = {0,1,2,3,4,5,6,7,8,9};
	
	public Subscriber get(String number) {
		return get(Long.parseLong(number));
	}
	
	public Subscriber get(Long number) {
		Subscriber p = cache.get(number);
		if (p==null) p = Subscriber.createNullObject();
		return p;
	}
	
	public List<Long> getAllPhones() {
		ArrayList<Long> list = new ArrayList<>(cache.keySet());
		return list;
	}

	public void startUp(int[] tacs) {
		Random rand = new Random();
		for (int i=0; i<NUMBER_OF_PHONES; i++) {
			int tacInx = rand.nextInt(tacs.length);
			
			long phoneNum = 0;
			boolean first = true;
			for (int j=0; j<NUMBER_OF_DIGITS_IN_NUMBER; j++) {
				int digit = numbers[rand.nextInt(numbers.length)];
				if (first) {
					while (digit==0) 
						digit = numbers[rand.nextInt(numbers.length)];
					first = false;
				}
				phoneNum = phoneNum*10 + digit;
			}
			Subscriber phone = new Subscriber(phoneNum, tacs[tacInx]);
			cache.put(phoneNum, phone);
		}
	}
}
