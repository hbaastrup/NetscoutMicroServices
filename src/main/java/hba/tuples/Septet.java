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

public class Septet<A,B,C,D,E,F,G> extends Tuple {
	public Septet(A arg0, B arg1, C arg2, D arg3, E arg4, F arg5, G arg6) {
		values.put(0, arg0);
		values.put(1, arg1);
		values.put(2, arg2);
		values.put(3, arg3);
		values.put(4, arg4);
		values.put(5, arg5);
		values.put(6, arg6);
	}
	
	public Type getType() {return Type.SEPTET;}
	
	@SuppressWarnings("unchecked")
	public A getValue0() {return (A)values.get(0);}
	@SuppressWarnings("unchecked")
	public B getValue1() {return (B)values.get(1);}
	@SuppressWarnings("unchecked")
	public C getValue2() {return (C)values.get(2);}
	@SuppressWarnings("unchecked")
	public D getValue3() {return (D)values.get(3);}
	@SuppressWarnings("unchecked")
	public E getValue4() {return (E)values.get(4);}
	@SuppressWarnings("unchecked")
	public F getValue5() {return (F)values.get(5);}
	@SuppressWarnings("unchecked")
	public G getValue6() {return (G)values.get(6);}
	
	public void setAt0(A arg) {values.put(0, arg);}
	public void setAt1(B arg) {values.put(1, arg);}
	public void setAt2(C arg) {values.put(2, arg);}
	public void setAt3(D arg) {values.put(3, arg);}
	public void setAt4(E arg) {values.put(4, arg);}
	public void setAt5(F arg) {values.put(5, arg);}
	public void setAt6(G arg) {values.put(6, arg);}
}
