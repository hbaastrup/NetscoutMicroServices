package playground.micro.models;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonGetter;
import playground.micro.models.TAC;
import playground.micro.subscriber.TacGetterDelegate;

public class Subscriber {
	private long number;
	private int tac;
	private TAC tacInfo = null;
	private long time = 0;
	
	public Subscriber(long number, int tac) {
		this.number = number;
		this.tac = tac;
	}
	
	public Subscriber setTAC(TAC tac) {
		this.tacInfo = tac;
		return this;
	}
	
	public static Subscriber createNullObject() {
		return new Subscriber(0L, 0);
	}
	
	public void addTime(int time) { //time in milliseconds
		this.time = time;
	}
	
	public void lasyLoad(TacGetterDelegate delegate) {
		if (tacInfo!=null) return;
		try {
			tacInfo = delegate.getTac(tac);
		} catch (InterruptedException | ExecutionException | IOException e) {
			e.printStackTrace();
			tacInfo = null;
		}
	}
	
	@JsonGetter("number")
	public long getNumber() {return number;}
	@JsonGetter("tac")
	public int getTac() {return tac;}
	@JsonGetter("info")
	public TAC getInfo() {return tacInfo;}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("{");
		str.append("\"number\":"+number);
		str.append(",\"tac\":"+tac);
		str.append(",\"time\":"+time);
		if (tacInfo!=null) {
			str.append(",\"info\":"+tacInfo.toString());
		}
		str.append("}");
		return str.toString();
	}
}
