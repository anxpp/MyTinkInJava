package com.anxpp.thinkinjava.chapter06;
/**
 * 吸血鬼数字
 * 算法略微复杂，而且，只能处理4位的数字
 * @author anxpp
 *
 */
public class Pratice10 {
	static int isEven(int n){
		int i =1;
		while(n/10>0){
			i++;
			n/=10;
		}
		return i;
	}
	public static void main(String args[]){
		VampireNumber vampireNumber = new VampireNumber();
		int i = 10000;
		while(--i>=1000)
			if(vampireNumber.isVampireNumber(i))
				System.out.println(i + " = " + vampireNumber.a + " * " + vampireNumber.b);
	}
}
/**
 * 吸血鬼数字
 * @author anxpp
 *
 */
class VampireNumber{
	int a;
	int b;
	//是否为吸血鬼数字
	boolean isVampireNumber(int n){
		if(n<10) return false;
		//是偶数位数的数字
		int len = getNumLen(n);
		if(!isEven(len))
			return false;
		//末尾两个部位0
		if(isEndWith00(n))
			return false;
		//判断是否为吸血鬼数字
		return isVampireNumber(n,len);
	}
	//判断吸血鬼数字
	boolean isVampireNumber(int n,int len){
//		System.out.println("测试");
		//这里处理4位数
		//拆分,个位开始
		int a[] = new int[len];
		a[0] = n%10;
		a[1] = n%100/10;
		a[2] = n/100%10;
		a[3] = n/1000;
		//组合测试,每个数字打头
		int i = len;
		while(--i>=0){
			int length = len;
			while(--length>=0){
				if(length == i) continue;
				//组合第一个数字
				int m = a[i]*10+a[length];//正序
				int o = a[i]+10*a[length];//反序
				int len2 = len;
				int p = -1,q = 0;
				while(--len2>=0){
					if(len2 == i||len2==length) continue;
					if(p==-1) p=a[len2];
					else{
						q=a[len2];
						break;
					}
				}
				//组合第二个数字
				int g = p*10+q;
				int h = p+10*q;
				if(isEquals(n,m,g)) return true;
				if(isEquals(n,m,h)) return true;
				if(isEquals(n,o,g)) return true;
				if(isEquals(n,o,h)) return true;
			}
		}
		return false;
	}
	boolean isEquals(int n,int x,int y){
		if(x*y == n){
			a = x;
			b = y;
			return true;
		}
		return false;
	}
	//计算数字的位数
	int getNumLen(int n){
		int i =1;
		while(n/10>0){
			i++;
			n/=10;
		}
		return i;
	}
	//是否是偶数
	boolean isEven(int n){
		if(n%2!=0)
			return false;
		return true;
	}
	//是否以2个0结尾
	boolean isEndWith00(int n){
		if(n%100==0)
			return true;
		return false;
	}
}
