package com.control.tools.onlyCheckBox;

import android.content.Context;
import android.view.Gravity;
import android.widget.CheckBox;

public class OnlyCheckBox extends CheckBox {
	public OnlyCheckBox(Context context,String name){
		super(context);
		setText(name);
		setGravity(Gravity.CENTER);
	}
}
