package com.control.component;

import com.control.component.IO.Input;
import com.control.component.IO.NoInputException;
import com.control.component.IO.Output;
import com.control.component.tools.Point;
import com.control.tools.DataType;
import com.control.tools.Ran;
import com.control.tools.sensor.SensorIn;
import com.control.tools.sensor.SensorOut;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

public class InComponentBSensor extends Component{
	private SensorIn si=null;
	public SensorIn getSensor() {
		return si;
	}
	public InComponentBSensor(Context context,int width,int sensorID) {
		super(context,new Point((float)Ran.getInt(150),(float)Ran.getInt(150)),width,"输入传感器"+sensorID);
		si=new SensorIn(sensorID);
	
	}
	public boolean addOutput(int pos){
		si.addPara(0);
		Output o=new Output(DataType.N){

			@Override
			public Object getOutputData() throws NoInputException {
				if(sArrayOutputPos==null) return si.getPara(0);
				Log.v("bear","sArrayOutputPos.size():"+sArrayOutputPos.size());
				return si.getPara(sArrayOutputPos.size()-1);
			}

			@Override
			protected void unbindProcess() {
				// TODO Auto-generated method stub
				
			}};
		return super.addOutput(pos, o);
	}
	public int getSensorID(){
		return si.getSensorID();
	}
}
