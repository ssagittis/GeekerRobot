package com.control.component;

import android.content.Context;
import android.util.SparseArray;

import com.control.component.IO.Input;
import com.control.component.IO.Output;
import com.control.component.tools.Point;
import com.control.tools.Ran;

public abstract class AbstrComponent extends Component {

	public AbstrComponent(Context context,  int width,String name) {
		super(context,new Point((float)Ran.getInt(150),(float)Ran.getInt(150)), width,name);
	}
	protected boolean addOutput(int pos,Output o){
		return super.addOutput(pos, o);
	}
	protected  boolean addInput(int pos,Input i){
		return super.addInput(pos, i);
	}
	
}
