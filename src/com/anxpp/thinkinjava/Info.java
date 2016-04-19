package com.anxpp.thinkinjava;
/**
 * Java编程思想学习记录
 * @author anxpp
 *
 */
public class Info {
	void f(short i){
		System.out.println("short");
	}
	void f(int i){
		System.out.println("int");
	}
	void f(long i){
		System.out.println("long");
	}
	void f(Integer i){
		System.out.println("Integer");
	}
	public static void main(String args[]){
		int a = 0;
		Integer b = 0;
		long c = 0;
		short d = 0;
		new Info().f(a);
		new Info().f(b);
		new Info().f(c);
		new Info().f(d);
		new Info().f(1);
		new Info().f(0);
	}
}
