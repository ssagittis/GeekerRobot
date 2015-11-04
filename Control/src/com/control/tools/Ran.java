package com.control.tools;

import java.util.Random;

public class Ran {
	private static final Random r=new Random();
	public static int getInt(){
		return r.nextInt();
	}
	public static int getInt(int n){
		return r.nextInt(n);
	}
}
