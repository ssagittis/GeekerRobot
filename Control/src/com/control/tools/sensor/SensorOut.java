package com.control.tools.sensor;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SensorOut extends Sensor{
	//private static HashMap<Integer,SensorOut> sensors=new HashMap<Integer,SensorOut>();
	//private String sensorname;		//传感器的名称
	//public String getSensorname() {
	//	return sensorname;
	//}
	private int sensorID;
	public int getSensorID() {
		return sensorID;
	}

	private boolean isConnected;	//此传感器是否连接上了
	private Vector<Integer> paras=new Vector<Integer>();			//传感器输出量的容器
	//private Vector<parameter> paras;			//传感器输出量的容器
	//private int paraNum = 0;		//传感器有几个输出量
	//public enum ParaType{			//传感器输出量的类型
	//	Digital,Analog
	//}
	/*private class parameter{
		private String paraName;
		private ParaType paraType;
		public int para;
		
		public parameter(String name,ParaType type){
			paraName = name;
			paraType = type;
		}
	}*/
	
	/*public Sensor(String name){
		sensorname = name;
		paras = new Vector<parameter>();
	}*/
	public SensorOut(int sensorID){
		this.sensorID=sensorID;
		//addSensor(this,sensorID);
	}
	/*public void delete(){
		for(Map.Entry<Integer, SensorOut> entry:sensors.entrySet()){    
		     if(entry.getValue()==this) sensors.remove(entry.getKey()) ; 
		}  
	}*/
	/*public void addPara(String name,ParaType type){
		paraNum++;
		parameter p =new parameter(name,type);
		paras.add(p);
	}*/
	public void addPara(int para){
		//paraNum++;
		//parameter p =new parameter(name,type);
		paras.add(para);
	}
	public int getParaNum() {
		return paras.size();
	}

	public int getPara(int paraID){
		return paras.elementAt(paraID);
	}
	
	public boolean setPara(int paraID,int para){
		if(paraID<paras.size()){
			paras.set(paraID, para);
			return true;
		}
		else{
			return false;
		}
	}
	/*public static int getSensorNum(){
		return sensors.size();
	}
	private boolean addSensor(SensorOut sensor,int sensorID){
		if(sensors.containsKey(sensorID)){
			return false;
		}
		else{
			sensors.put(sensorID, sensor);
			return true;	
		}
	}
	public static SensorOut getSensor(int sensorID){
		return sensors.get(sensorID);
	}
	public static void empty(){
		sensors = null;
		sensors = new HashMap<Integer,SensorOut>();
	}*/
}
