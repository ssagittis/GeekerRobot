package com.control.widget;

import com.control.ComponentsService;
import com.control.NetSensorService;
import com.control.component.Component;
import com.control.tools.DataType;

import android.content.Context;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class CheckBoxWidg extends CheckBox implements InWidget {
	private Context c;
	public CheckBoxWidg(Context context) {
		super(context);
		c=context;
		this.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Intent intent = new Intent();  
	            intent.setAction(ComponentsService.actionComRenew);
				c.sendBroadcast(intent);
				intent = new Intent(); 
				intent.setAction(NetSensorService.actionSenOutRenew);
				c.sendBroadcast(intent);
			}
		});
	}

	@Override
	public Object getOutput() {
		return isChecked();
	}

	@Override
	public int getDataType() {
		return DataType.B;
	}

	@Override
	public void unbindCom() {
		
	}

}
