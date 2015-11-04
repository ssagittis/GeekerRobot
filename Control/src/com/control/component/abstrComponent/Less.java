package com.control.component.abstrComponent;

import android.content.Context;

import com.control.component.AbstrComponent;
import com.control.component.IO.Input;
import com.control.component.IO.NoInputException;
import com.control.component.IO.Output;
import com.control.component.tools.Point;
import com.control.tools.DataType;

public class Less extends AbstrComponent {

	public Less(Context context,int width,int inputPos1,int inputPos2,int outputPos1) {
		super(context, width,"小于组件");
		final Input i1=new Input(DataType.N){

			@Override
			protected void unbindProcess() {
				// TODO Auto-generated method stub
				
			}
			
		};
		final Input i2=new Input(DataType.N){

			@Override
			protected void unbindProcess() {
				// TODO Auto-generated method stub
				
			}
			
		};
		Output o=new Output(DataType.B){

			@Override
			public Object getOutputData() throws NoInputException {
				if(((Integer)i1.getOutputBind().getOutputData()).intValue()<((Integer)i2.getOutputBind().getOutputData()).intValue()) return true;
				else return false;
			}

			@Override
			protected void unbindProcess() {
				// TODO Auto-generated method stub
				
			}
			
		};
		addInput(0,i1);
		addInput(1,i2);
		addOutput(1,o);
	}
	protected boolean addOutput(int pos,Output o){
		return super.addOutput(pos, o);
	}
	protected boolean addInput(int pos,Input i){
		return super.addInput(pos, i);
	}
}