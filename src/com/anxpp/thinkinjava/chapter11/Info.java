﻿package com.anxpp.thinkinjava.chapter11;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

/**
 * 第十一章 持有对象
 * @author anxpp
 *
 */
public class Info {
	Collection<?> c;
	List<?> l;
	int[] empty={};
	int is[];
	Map<?, ?> p;
	HashMap<?, ?> hm;
	Queue<?> q;
	ListIterator<?> oo;
	TreeSet<Object> t = new TreeSet<>();
	Iterator<?> it;
	LinkedList<?> lin;
	Vector<?> v;
	Stack<?> ssss;
	HashSet<?> hs;
	Hashtable<?, ?> ht;
	LinkedHashMap<?, ?> lh;
	Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    transient Object[] elementData; // non-private to simplify nested class access
	void test(){
//		Iterator<?> iter = hm.entrySet().iterator();
	}
}