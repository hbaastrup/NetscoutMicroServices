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

public class Triplet<A,B,C> extends Tuple {
	
	public Triplet(A arg0, B arg1, C arg2) {
		values.put(0, arg0);
		values.put(1, arg1);
		values.put(2, arg2);
	}
	
	public Type getType() {return Type.TRIPLET;}
	
	@SuppressWarnings("unchecked")
	public A getValue0() {return (A)values.get(0);}
	@SuppressWarnings("unchecked")
	public B getValue1() {return (B)values.get(1);}
	@SuppressWarnings("unchecked")
	public C getValue2() {return (C)values.get(2);}
	
	public void setAt0(A arg) {values.put(0, arg);}
	public void setAt1(B arg) {values.put(1, arg);}
	public void setAt2(C arg) {values.put(2, arg);}
}
