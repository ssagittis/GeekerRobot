package com.control.widget;

import com.control.component.Component;


public interface OutWidget {
	public void setInput(Object o);//往控件里写入数据
	public int getDataType();//获得控件的类型
	public void unbindCom();
}
