package com.control.component.abstrComponent;

import java.util.Vector;

import android.content.Context;

import com.control.component.AbstrComponent;
import com.control.component.IO.Input;
import com.control.component.IO.NoInputException;
import com.control.component.IO.Output;
import com.control.component.tools.Point;

public class Shunt extends AbstrComponent {

	public Shunt(Context context, int dataType,int width,int inputPos,Vector<Integer> vOutputPos) {
		super(context, width,"分路组件");
		final Input i=new Input(dataType){

			@Override
			protected void unbindProcess() {
				// TODO Auto-generated method stub
				
			}
			
		};
		addInput(inputPos,i);
		for(int j=0;j<vOutputPos.size();j++){
			Output o=new Output(dataType){

				@Override
				public Object getOutputData() throws NoInputException {
					return i.getOutputBind().getOutputData();
				}

				@Override
				protected void unbindProcess() {
					// TODO Auto-generated method stub
					
				}
				
			};
			addOutput(vOutputPos.elementAt(j),o);
			
		}
	}
	protected boolean addOutput(int pos,Output o){
		return super.addOutput(pos, o);
	}
	protected boolean addInput(int pos,Input i){
		return super.addInput(pos, i);
	}
}
