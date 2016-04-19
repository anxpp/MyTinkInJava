package com.anxpp.thinkinjava.chapter11.Iterable;

import java.util.Iterator;

public class Cigarette implements Iterable<Cigarette> {
	
	String name;
	Cigarette[] cigarette;

	@Override
	public Iterator<Cigarette> iterator() {
		return new MyIterator() 	;
	}
	
	class MyIterator implements Iterator<Cigarette>{
		//�൱������
		private int index = 0;
		@Override
		public boolean hasNext() {
			return index!=cigarette.length;
		}
		@Override
		public Cigarette next() {
			return cigarette[index++];
		}
		
	}

}