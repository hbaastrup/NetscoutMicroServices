package playground.micro.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

public class TACArray {
	int[] tacs;
	
	public TACArray() {}
	
	public TACArray(int[] tacs) {
		this.tacs = tacs;
	}
	
	public TACArray(List<Integer> tacs) {
		this.tacs = new int[tacs.size()];
		for (int i=0; i<this.tacs.length; i++)
			this.tacs[i] = tacs.get(i);
	}
	
	@JsonGetter("tacs")
	public int[] getTacs() {
		return tacs;
	}

}
