package com.control.widget;

import com.control.ComponentsService;
import com.control.NetSensorService;
import com.control.component.Component;
import com.control.tools.DataType;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.SeekBar;

public class SeekBarWidg extends SeekBar implements InWidget {
	private Context c;
	public SeekBarWidg(Context context) {
		super(context);
		c=context;
		this.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
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
		return this.getProgress();
	}

	@Override
	public int getDataType() {
		return DataType.N;
	}

	@Override
	public void unbindCom() {
		
	}
}
