package hba.tuples;
/*
 * Copyright 2018 Henrik Baastrup
 *
 * Licensed under GNU Lesser General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public abstract class Tuple {
	public enum Type {UNIT,PAIR,TRIPLET,QUARTET,QUINTET,SEXTET,SEPTET,OCTET,ENNEAD,DECADE};
	
	Hashtable<Integer, Object> values = new Hashtable<>();
	
	public Object getValue(int inx) {
		if (inx > values.size())
			throw new IllegalArgumentException("Index out of range");
		return values.get(inx);
	}
	
	public List<Object> toList() {
		ArrayList<Object> list = new ArrayList<>();
		for (int i=0; i<values.size(); i++) {
			Object o = values.get(i);
			list.add(o);
		}
		return list;
	}
	
	public Object[] toArray() {
		Object[] array = new Object[values.size()];
		for (int i=0; i<values.size(); i++) {
			Object o = values.get(i);
			array[i] = o;
		}
		return array;
	}
	
	public abstract Type getType();
	
	public Tuple add(Tuple t) {
		return join(this, t);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Tuple join(Tuple t1, Tuple t2) {
		switch (t1.getType()) {
		case UNIT:
			switch (t2.getType()) {
			case UNIT:
				return new Pair(t1.getValue(0),t2.getValue(0));
			case PAIR:
				return new Triplet(t1.getValue(0),t2.getValue(0),t2.getValue(1));
			case TRIPLET:
				return new Quartet(t1.getValue(0),t2.getValue(0),t2.getValue(1),t2.getValue(2));
			case QUARTET:
				return new Quintet(t1.getValue(0),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3));
			case QUINTET:
				return new Sextet(t1.getValue(0),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4));
			case SEXTET:
				return new Septet(t1.getValue(0),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4),t2.getValue(5));
			case SEPTET:
				return new Octet(t1.getValue(0),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4),t2.getValue(5),t2.getValue(6));
			case OCTET:
				return new Ennead(t1.getValue(0),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4),t2.getValue(5),t2.getValue(6),t2.getValue(7));
			case ENNEAD:
				return new Decade(t1.getValue(0),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4),t2.getValue(5),t2.getValue(6),t2.getValue(7),t2.getValue(8));
			default:
				throw new IllegalArgumentException("There does not exists a tuple big enough to join the two give tuples");
			}
		case PAIR:
			switch (t2.getType()) {
			case UNIT:
				return new Triplet(t1.getValue(0),t1.getValue(1),t2.getValue(0));
			case PAIR:
				return new Quartet(t1.getValue(0),t1.getValue(1),t1.getValue(0),t2.getValue(1));
			case TRIPLET:
				return new Quintet(t1.getValue(0),t1.getValue(1),t2.getValue(0),t2.getValue(1),t2.getValue(2));
			case QUARTET:
				return new Sextet(t1.getValue(0),t1.getValue(1),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3));
			case QUINTET:
				return new Septet(t1.getValue(0),t1.getValue(1),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4));
			case SEXTET:
				return new Octet(t1.getValue(0),t1.getValue(1),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4),t2.getValue(5));
			case SEPTET:
				return new Ennead(t1.getValue(0),t1.getValue(1),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4),t2.getValue(5),t2.getValue(6));
			case OCTET:
				return new Decade(t1.getValue(0),t1.getValue(1),t2.getValue(1),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4),t2.getValue(5),t2.getValue(6),t2.getValue(7));
			default:
				throw new IllegalArgumentException("There does not exists a tuple big enough to join the two give tuples");
			}
		case TRIPLET:
			switch (t2.getType()) {
			case UNIT:
				return new Quartet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t2.getValue(0));
			case PAIR:
				return new Quintet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t2.getValue(0),t2.getValue(1));
			case TRIPLET:
				return new Sextet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t2.getValue(0),t2.getValue(1),t2.getValue(2));
			case QUARTET:
				return new Septet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3));
			case QUINTET:
				return new Octet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4));
			case SEXTET:
				return new Ennead(t1.getValue(0),t1.getValue(1),t1.getValue(2),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4),t2.getValue(5));
			case SEPTET:
				return new Decade(t1.getValue(0),t1.getValue(1),t1.getValue(2),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4),t2.getValue(5),t2.getValue(6));
			default:
				throw new IllegalArgumentException("There does not exists a tuple big enough to join the two give tuples");
			}
		case QUARTET:
			switch (t2.getType()) {
			case UNIT:
				return new Quintet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t2.getValue(0));
			case PAIR:
				return new Sextet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t2.getValue(0),t2.getValue(1));
			case TRIPLET:
				return new Septet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t2.getValue(0),t2.getValue(1),t2.getValue(2));
			case QUARTET:
				return new Octet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3));
			case QUINTET:
				return new Ennead(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4));
			case SEXTET:
				return new Decade(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4),t2.getValue(5));
			default:
				throw new IllegalArgumentException("There does not exists a tuple big enough to join the two give tuples");
			}
		case QUINTET:
			switch (t2.getType()) {
			case UNIT:
				return new Sextet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t2.getValue(0));
			case PAIR:
				return new Septet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t2.getValue(0),t2.getValue(1));
			case TRIPLET:
				return new Octet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t2.getValue(0),t2.getValue(1),t2.getValue(2));
			case QUARTET:
				return new Ennead(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3));
			case QUINTET:
				return new Decade(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3),t2.getValue(4));
			default:
				throw new IllegalArgumentException("There does not exists a tuple big enough to join the two give tuples");
			}
		case SEXTET:
			switch (t2.getType()) {
			case UNIT:
				return new Septet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t1.getValue(5),t2.getValue(0));
			case PAIR:
				return new Octet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t1.getValue(5),t2.getValue(0),t2.getValue(1));
			case TRIPLET:
				return new Ennead(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t1.getValue(5),t2.getValue(0),t2.getValue(1),t2.getValue(2));
			case QUARTET:
				return new Decade(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t1.getValue(5),t2.getValue(0),t2.getValue(1),t2.getValue(2),t2.getValue(3));
			default:
				throw new IllegalArgumentException("There does not exists a tuple big enough to join the two give tuples");
			}
		case SEPTET:
			switch (t2.getType()) {
			case UNIT:
				return new Octet(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t1.getValue(5),t1.getValue(6),t2.getValue(0));
			case PAIR:
				return new Ennead(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t1.getValue(5),t1.getValue(6),t2.getValue(0),t2.getValue(1));
			case TRIPLET:
				return new Decade(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t1.getValue(5),t1.getValue(6),t2.getValue(0),t2.getValue(1),t2.getValue(2));
			default:
				throw new IllegalArgumentException("There does not exists a tuple big enough to join the two give tuples");
			}
		case OCTET:
			switch (t2.getType()) {
			case UNIT:
				return new Ennead(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t1.getValue(5),t1.getValue(6),t1.getValue(7),t2.getValue(0));
			case PAIR:
				return new Decade(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t1.getValue(5),t1.getValue(6),t1.getValue(7),t2.getValue(0),t2.getValue(1));
			default:
				throw new IllegalArgumentException("There does not exists a tuple big enough to join the two give tuples");
			}
		case ENNEAD:
			switch (t2.getType()) {
			case UNIT:
				return new Decade(t1.getValue(0),t1.getValue(1),t1.getValue(2),t1.getValue(3),t1.getValue(4),t1.getValue(5),t1.getValue(6),t1.getValue(7),t1.getValue(8),t2.getValue(0));
			default:
				throw new IllegalArgumentException("There does not exists a tuple big enough to join the two give tuples");
			}
		default:
			throw new IllegalArgumentException("There does not exists a tuple big enough to join the two give tuples");
		}
	}

	
	@Override
	public int hashCode() {
		int hash = 0;
		for (Object o : values.values()) {
			hash = hash ^ o.hashCode();
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		 if (!(obj instanceof Tuple)) return false;
		 Tuple t = (Tuple)obj;
		 for (int i=0; i<values.size(); i++) {
			 if (!values.get(i).equals(t.getValue(i)))
				 return false;
		 }
		 return true;
	}
	
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("("+values.get(0).toString());
		for (int i=1; i<values.size(); i++)
			str.append(", "+getValue(i));
		str.append(")");
		return str.toString();
	}


}
