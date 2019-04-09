package playground.micro.kpi;

public class TimeTaken extends KpiBase {
	final int time; //in milliseconds
	
	public TimeTaken(int time) {
		super();
		this.time = time;
	}
	
	public int getTime() {return time;}
}
