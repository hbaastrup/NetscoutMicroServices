package playground.micro.models;

public class CDR {
	public enum STATUS {OK, ERROR};
	
	long calling;
	long called;
	long beginTime;
	long endTime;
	STATUS status;
	
	public CDR() {}
	
	public CDR(long calling, long called, long begin, long end) {
		this.calling = calling;
		this.called = called;
		this.beginTime = begin;
		this.endTime = end;
		this.status = STATUS.OK;
	}
	
	public CDR(long calling, long called, long time) {
		this.calling = calling;
		this.called = called;
		this.beginTime = time;
		this.endTime = time;
		this.status = STATUS.ERROR;
	}
	
	public long getCalling() {return calling;}
	
	public long getCalled() {return called;}
	
	public long getBeginTime() {return beginTime;}
	
	public long getEndTime() {return endTime;}
	
	public int getCalledTime() {
		if (endTime > beginTime) return (int)(endTime - beginTime);
		else return (int)(beginTime - endTime);
	}
	
	public STATUS getStatus() {return status;}
	
	@Override
	public String toString() {
		return "{\"calling\":"+calling+" ,\"begin\": "+beginTime+" ,\"end\":"+endTime+" ,\"status\": "+status+"}";
	}
}
