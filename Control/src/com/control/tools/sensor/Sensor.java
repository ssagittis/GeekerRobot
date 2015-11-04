package com.control.tools.sensor;

import java.util.Vector;

public class Sensor {
	public static final int INITPARASNUM=8;
	private Vector<Integer> initParas=new Vector<Integer>();
	public void addInitParas(int initPara){
		initParas.add(initPara);
	}
	public int getInitParas(int pos){
		return initParas.elementAt(pos);
	}
}
