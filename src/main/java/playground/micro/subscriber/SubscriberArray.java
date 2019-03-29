package playground.micro.subscriber;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

public class SubscriberArray {
	long[] numbers;
	
	public SubscriberArray() {}
	
	public SubscriberArray(List<Long> phones) {
		numbers = new long[phones.size()];
		for (int i=0; i<numbers.length; i++)
			numbers[i] = phones.get(i);
	}
	
	@JsonGetter("numbers")
	public long[] getTacs() {
		return numbers;
	}

}
