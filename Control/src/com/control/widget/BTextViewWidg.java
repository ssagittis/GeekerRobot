package com.control.widget;

import com.control.ComponentsService;
import com.control.component.Component;
import com.control.tools.DataType;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class BTextViewWidg extends TextView implements OutWidget {
	public BTextViewWidg(Context context) {
		super(context);
		setBackgroundColor(Color.WHITE);   
	}
	@Override
	public void setInput(Object o) {
		this.setText(o.toString());
	}

	@Override
	public int getDataType() {
		return DataType.B;
	}
	@Override
	public void unbindCom() {
		
	}
}
