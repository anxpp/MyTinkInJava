package com.anxpp.thinkinjava.chapter11;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;

/**
 * 第十一章 持有对象
 * @author anxpp
 *
 */
public class Info {
	Collection<?> c;
	List<?> l;
	Map<?, ?> p;
	HashMap<?, ?> hm;
	Queue<?> q;
	ListIterator<?> oo;
	TreeSet<Object> t = new TreeSet<>();
	void test(){
	}
}