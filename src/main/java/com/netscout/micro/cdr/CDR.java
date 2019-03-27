package com.netscout.micro.cdr;

public class CDR {
	public enum STATUS {OK, ERROR};
	
	long subscriber;
	long beginTime;
	long endTime;
	STATUS status;
	
	public CDR() {}
	
	public CDR(long sub, long begin, long end) {
		this.subscriber = sub;
		this.beginTime = begin;
		this.endTime = end;
		this.status = STATUS.OK;
	}
	
	public CDR(long sub, long time) {
		this.subscriber = sub;
		this.beginTime = time;
		this.endTime = time;
		this.status = STATUS.ERROR;
	}
	
	public long getSubscriber() {return subscriber;}
	
	public long getBeginTime() {return beginTime;}
	
	public long getEndTime() {return endTime;}
	
	public STATUS getStatus() {return status;}
	
	@Override
	public String toString() {
		return "{\"subscriber\":"+subscriber+" ,\"begin\": "+beginTime+" ,\"end\":"+endTime+" ,\"status\": "+status+"}";
	}
}
