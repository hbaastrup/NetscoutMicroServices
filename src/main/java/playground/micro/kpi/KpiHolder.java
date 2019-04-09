package playground.micro.kpi;

import java.util.Hashtable;

import hba.tuples.Pair;

public enum KpiHolder {
	INSTANCE;
	
	Hashtable<String, DataSeries> dataSeries = new Hashtable<>();
	
	public void add(String key, KpiBase value) {
		DataSeries series = dataSeries.get(key);
		if (series==null) throw new NullPointerException("A data serie for "+key+" does not exist. Add it before to call this method");
		series.add(value);
	}
	
	public void add(String key, DataSeries series) {
		dataSeries.put(key, series);
	}
	
	public Pair<Double, Integer> getAvarage(String key) {
		DataSeries series = dataSeries.get(key);
		if (series==null) throw new NullPointerException("A data serie for "+key+" does not exist. Add it before to call this method");
		return series.getAvarage();
	}

}
