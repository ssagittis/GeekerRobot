package com.control.widget;

import com.control.component.Component;

public interface InWidget{
	public Object getOutput();//获得控件的数据，强制转化为Object类型
	public int getDataType();//获得数据类型
	public void unbindCom();
}
