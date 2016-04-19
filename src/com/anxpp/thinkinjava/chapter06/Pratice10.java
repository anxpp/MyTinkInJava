package com.anxpp.thinkinjava.chapter06;
/**
 * ��Ѫ������
 * �㷨��΢���ӣ����ң�ֻ�ܴ���4λ������
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
 * ��Ѫ������
 * @author anxpp
 *
 */
class VampireNumber{
	int a;
	int b;
	//�Ƿ�Ϊ��Ѫ������
	boolean isVampireNumber(int n){
		if(n<10) return false;
		//��ż��λ��������
		int len = getNumLen(n);
		if(!isEven(len))
			return false;
		//ĩβ������λ0
		if(isEndWith00(n))
			return false;
		//�ж��Ƿ�Ϊ��Ѫ������
		return isVampireNumber(n,len);
	}
	//�ж���Ѫ������
	boolean isVampireNumber(int n,int len){
//		System.out.println("����");
		//���ﴦ��4λ��
		//���,��λ��ʼ
		int a[] = new int[len];
		a[0] = n%10;
		a[1] = n%100/10;
		a[2] = n/100%10;
		a[3] = n/1000;
		//��ϲ���,ÿ�����ִ�ͷ
		int i = len;
		while(--i>=0){
			int length = len;
			while(--length>=0){
				if(length == i) continue;
				//��ϵ�һ������
				int m = a[i]*10+a[length];//����
				int o = a[i]+10*a[length];//����
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
				//��ϵڶ�������
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
	//�������ֵ�λ��
	int getNumLen(int n){
		int i =1;
		while(n/10>0){
			i++;
			n/=10;
		}
		return i;
	}
	//�Ƿ���ż��
	boolean isEven(int n){
		if(n%2!=0)
			return false;
		return true;
	}
	//�Ƿ���2��0��β
	boolean isEndWith00(int n){
		if(n%100==0)
			return true;
		return false;
	}
}
