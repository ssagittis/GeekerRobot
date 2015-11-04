package com.control.component;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import com.control.component.IO.Output;
import com.control.component.tools.Point;
import com.control.tools.DataType;
import com.control.tools.Ran;
import com.control.widget.InWidget;

public class InComponentBWidget extends Component {
	private InWidget in;
	public InComponentBWidget(Context context,InWidget inin) {
		this(context, inin, "");
	}
	public InComponentBWidget(Context context,InWidget inin,String name) {
		super(context,new Point((float)Ran.getInt(150),(float)Ran.getInt(150)),1,name);
		in=inin;
		Output o=new Output(in.getDataType()){

			@Override
			public Object getOutputData() {
				return in.getOutput();
			}

			@Override
			protected void unbindProcess() {
				in.unbindCom();
			}
			
		};
		addOutput(0,o);
	}
}
