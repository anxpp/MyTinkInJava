package com.anxpp.thinkinjava.chapter08;

/**
 * µÚ°ËÕÂ ¶àÌ¬
 * @author anxpp
 *
 */
public class Info {
	public static void main(String args[]){
		Super sup = new Sub();
		System.out.println(sup.i);
		System.out.println(sup.getI());
	}
}
class Sub extends Super{
	int i = 1;
	int getI(){
		return i;
	}
}
class Super{
	int i = 0;
	int getI(){
		return i;
	}
}
class MyClass implements Interface1,Interface2{

	@Override
	public int print() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int print(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

}
interface Interface1{
	int print(int i);
}
interface Interface2{
	int print();
}