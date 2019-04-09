package playground.micro.kpi;

import hba.tuples.Pair;

public interface DataSeries {
	
	public Pair<Double, Integer> getAvarage();
	public void add(KpiBase value);
	
	

}
