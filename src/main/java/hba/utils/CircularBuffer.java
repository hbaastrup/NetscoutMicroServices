package hba.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Fixed size buffer that overwrites previous entries after filling up all slots.
 * @author hbaastrup
 *
 */
public class CircularBuffer<T> {
	private final AtomicInteger nextIndex;
	private final Object[] slots;
	private final int mask;

	public CircularBuffer(int size) {
		if (size < 0) throw new IllegalArgumentException("Illegal initial capacity: " + size);
		if (size < 4) size = 4;
		nextIndex = new AtomicInteger(0);
		int capacity = 1;
        while (capacity < size) capacity <<= 1;
		slots = new Object[capacity];
		for (int i=0; i<capacity; i++) slots[i] = null;
		mask = capacity - 1;
	}
	
	public void add(T item) {
		int i = nextIndex.getAndIncrement() & slots.length;
		slots[i] = item;
	}
	
	@SuppressWarnings("unchecked")
	public T get(int index) {
		return (T)slots[index & mask];
	}
	
	public int size() {return slots.length;}
	
	public List<T> toList() {
		ArrayList<T> items = new ArrayList<>(slots.length);
		for (int i = 0; i < slots.length; ++i) {
			T item = get(i);
			if (item != null)
				items.add(item);
		}
		return items;
	}
}
