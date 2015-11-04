package com.control.widget;

import com.control.ComponentsService;
import com.control.NetSensorService;
import com.control.component.Component;
import com.control.tools.DataType;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EditTextWidg extends EditText  implements InWidget {
	private Context c;
	public EditTextWidg(Context context) {
		super(context);
		c=context;
		setKeyListener(new DigitsKeyListener(false,true));
		this.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
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
		try{
			return Integer.parseInt(this.getText().toString());
		}catch(Exception e){
			return 0;
		}
	}

	@Override
	public int getDataType() {
		return DataType.N;
	}

	@Override
	public void unbindCom() {
		
	}
}
