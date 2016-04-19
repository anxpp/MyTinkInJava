package com.anxpp.thinkinjava.chapter03;
/**
 * 
 * @author anxpp
 *
 */
public class Pratice14 {
	static void compare(String a,String b){
		System.out.println(a==b);
		System.out.println(a.equals(b));
	}
	public static void main(String args[]){
		String a = "abc";
		String b = "abc";
		String c = new String("abc");
		compare(a,b);
		compare(a,c);
	}
}
