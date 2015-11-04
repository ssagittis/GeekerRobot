package com.control.component;

import android.content.Context;
import android.util.SparseArray;

import com.control.component.IO.Input;
import com.control.component.IO.Output;
import com.control.component.tools.Point;
import com.control.tools.Ran;
import com.control.widget.InWidget;
import com.control.widget.OutWidget;

public class OutComponentBWidget extends Component {
	private Input i;
	private OutWidget out;
	public OutWidget getOutWidg() {
		return out;
	}
	public Input getInput() {
		return i;
	}
	public OutComponentBWidget(Context context,OutWidget outin) {
		this(context, outin, "");
	}
	public OutComponentBWidget(Context context,OutWidget outin,String name) {
		super(context,new Point((float)Ran.getInt(150),(float)Ran.getInt(150)),1,name);
		out=outin;
		i=new Input(out.getDataType()){

			@Override
			protected void unbindProcess() {
				out.unbindCom();
				
			}
			
		};
		addInput(0,i);
	}
}
