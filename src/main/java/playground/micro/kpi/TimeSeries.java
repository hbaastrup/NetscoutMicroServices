package playground.micro.kpi;

import java.util.ArrayList;

import hba.tuples.Pair;

public class TimeSeries implements DataSeries {
	final int duration; //in milliseconds
	
	ArrayList<TimeTaken> times = new ArrayList<>();

	public TimeSeries(int duration) {
		this.duration = duration;
	}
	
	public int getDuration() {return duration;}
	
	@Override
	public void add(KpiBase value) {
		if (value instanceof TimeTaken)
			add((TimeTaken)value);
		else
			throw new IllegalArgumentException();
	}
	
	public void add(TimeTaken time) {
		times.add(time);
	}
	
	@Override
	public Pair<Double, Integer> getAvarage() {
		return getAvarage(System.currentTimeMillis());
	}
	
	public Pair<Double, Integer> getAvarage(long now) {
		long olderThan = now - duration;
		
		long sum = 0;
		int count = 0;
		for (TimeTaken tt: times) {
			if (tt.getTimestamp() < olderThan) continue;
			sum += tt.getTime();
			count++;
		}
		if (count==0) return new Pair<Double, Integer>(0.0, 0);
		return new Pair<Double, Integer>((double)sum/count, count);
	}
	
	public void cleanup(long now) {
		long olderThan = now - duration;
		
		ArrayList<TimeTaken> toDelete = new ArrayList<>();
		for (TimeTaken tt: times) {
			if (tt.getTimestamp() < olderThan)
				toDelete.add(tt);
		}
		
		for (TimeTaken tt : toDelete) {
			times.remove(tt);
		}
	}

}
