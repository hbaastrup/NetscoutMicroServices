package playground.micro.models;

import com.fasterxml.jackson.annotation.JsonGetter;

public class TAC {
	int tac;
	String brand;
	String model;
	String type;
	
	public TAC() {}
	
	public TAC(String tac, String brand,String model, String type) {
		this(toInt(tac), brand, model, type);
	}
	
	public TAC(int tac, String brand,String model, String type) {
		this.tac = tac;
		this.brand = brand;
		this.model = model;
		this.type = type;
	}
	
	@JsonGetter("tac")
	public long getTac() {return tac;}
	@JsonGetter("brand")
	public String getBrand() {return brand;}
	@JsonGetter("model")
	public String getModel() {return model;}
	@JsonGetter("type")
	public String getType() {return type;}
	
	public static int toInt(String tac) {
		return Integer.parseInt(tac);
	}
	
	public static TAC createNullObject() {
		return new TAC(0,"","","");
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("{");
		str.append("\"tac\":"+tac);
		str.append(", \"brand\":\""+brand+"\"");
		str.append(", \"model\":\""+model+"\"");
		str.append(", \"type\":\""+type+"\"");
		str.append("}");
		return str.toString();
	}
}
