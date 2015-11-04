package com.control.widget;

import com.control.component.Component;
import com.control.tools.DataType;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

public class NTextViewWidg extends TextView implements OutWidget {
	public NTextViewWidg(Context context) {
		super(context);
		setBackgroundColor(Color.WHITE);
	}
	@Override
	public void setInput(Object o) {
		this.setText(o.toString());
	}

	@Override
	public int getDataType() {
		return DataType.N;
	}
	@Override
	public void unbindCom() {
		
	}
}
