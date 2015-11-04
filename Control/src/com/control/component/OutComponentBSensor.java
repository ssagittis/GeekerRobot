package com.control.component;

import com.control.component.IO.Input;
import com.control.component.IO.Output;
import com.control.component.tools.Point;
import com.control.tools.Ran;
import com.control.tools.sensor.SensorOut;

import android.content.Context;
import android.util.SparseArray;

public class OutComponentBSensor extends Component{
	private SensorOut so=null;
	public SensorOut getSensor() {
		return so;
	}
	public void setSensor(SensorOut so) {
		this.so = so;
	}
	public OutComponentBSensor(Context context,int width,int sensorID) {
		super(context,
				new Point((float)Ran.getInt(150),(float)Ran.getInt(150)),
				width,"输出传感器"+sensorID);
		so=new SensorOut(sensorID);
	}
	public boolean addInput(int pos,Input i){
		so.addPara(0);
		return super.addInput(pos, i);
	}
	public int getSensorID(){
		return so.getSensorID();
	}
	public SparseArray<Input> getSArrayInputPos(){
		return sArrayInputPos;
	}
}
