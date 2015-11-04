package com.control.widget;
import com.control.ControlActivity;
import com.control.ProgramActivity;
import com.control.VideoActivity;
import com.control.tools.DataType;
import com.control.tools.VideoData;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class VideoButton extends Button implements OutWidget{
	private boolean isBind=false;//是否绑定了组件
	private Context c;
	public VideoButton(Context context) {
		super(context);
		c=context;
		this.setText("视频流");
		this.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//if(isBind){
				if(true){
					//可用sp来存储指定摄像头，目前只假定有一个摄像头
					Intent intent=new Intent(c,VideoActivity.class); 
		            c.startActivity(intent);
				}
			}
			
		});
	}
	@Override
	public void setInput(Object o) {
		if(o  instanceof VideoData) isBind=true;
		else isBind=false;
	}

	@Override
	public int getDataType() {
		return DataType.V;
	}
	@Override
	public void unbindCom() {
		isBind=false;
	}

}
