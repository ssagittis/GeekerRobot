package com.control.component.abstrComponent;

import android.content.Context;

import com.control.component.AbstrComponent;
import com.control.component.IO.Input;
import com.control.component.IO.NoInputException;
import com.control.component.IO.Output;
import com.control.tools.DataType;

public class Constant extends AbstrComponent {
	public Constant(Context context,final int constant) {
		super(context,1,"常数"+constant);
		Output o=new Output(DataType.N){

			@Override
			public Object getOutputData() throws NoInputException {
				return constant;
			}

			@Override
			protected void unbindProcess() {
				// TODO Auto-generated method stub
				
			}
			
		};
		addOutput(0,o);
	}
	public Constant(Context context,final boolean constant) {
		super(context,1,"常数"+constant);
		Output o=new Output(DataType.B){

			@Override
			public Object getOutputData() throws NoInputException {
				return constant;
			}

			@Override
			protected void unbindProcess() {
				// TODO Auto-generated method stub
				
			}
			
		};
		addOutput(0,o);
	}
}
