package com.control;

import java.util.Map;

import com.control.ControlActivity.WidgetL;
import com.control.tools.onlyCheckBox.OnlyCheckBox;
import com.control.tools.onlyCheckBox.OnlyCheckBoxHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddWidgetActivity extends Activity {
	Context context=this;
	EditText nameEdit;
	private static final String[] arrayInWidgetType={//注意和ControlActivity里的实际控件对应起来
		"布尔值输入",
		"值输入（文本框）",
		"值输入（拖动条）"
	};
	private static final String[] arrayOutWidgetType={//注意和ControlActivity里的实际控件对应起来
		"值输出",
		"布尔值输出",
		"视频输出"
	};
	private OnlyCheckBoxHelper onlyCheckBoxHelper=new OnlyCheckBoxHelper();
	//private OnlyCheckBox arrayOnlyCheckBox[]=new OnlyCheckBox[arrayInWidgetType.length+arrayOutWidgetType.length];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_widget);
		
		LinearLayout inWidgetList=(LinearLayout)this.findViewById(R.id.inWidgetList);
		LinearLayout outWidgetList=(LinearLayout)this.findViewById(R.id.outWidgetList);
		nameEdit=(EditText)this.findViewById(R.id.nameEdit);
		for(int i=0;i<arrayInWidgetType.length;i++){
			//arrayOnlyCheckBox[i]=new OnlyCheckBox(this,arrayInWidgetType[i]);
			OnlyCheckBox c=new OnlyCheckBox(this,arrayInWidgetType[i]);
			onlyCheckBoxHelper.add(c);
			inWidgetList.addView(c);
		}
		for(int i=0;i<arrayOutWidgetType.length;i++){
			OnlyCheckBox c=new OnlyCheckBox(this,arrayOutWidgetType[i]);
			//arrayOnlyCheckBox[i+arrayInWidgetType.length]=new OnlyCheckBox(this,arrayOutWidgetType[i]);
			onlyCheckBoxHelper.add(c);
			outWidgetList.addView(c);
		}
		Button add=((Button)this.findViewById(R.id.add));
		add.setOnClickListener(new Button.OnClickListener(){
			 public void onClick(View v){ 
				 Message msg = new Message();
				 msg.what=onlyCheckBoxHelper.indexOfWhichIsChecked();
				 mHandler.sendMessage(msg);
			 }
		}); 
	}
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what>=onlyCheckBoxHelper.size()||msg.what<0||nameEdit.getText().toString().length()==0){
				Toast.makeText(context, "名字没填或者没选控件类型囧",
					    Toast.LENGTH_SHORT).show();
			}else if(isNameExist()){
				Toast.makeText(context, "名字chongfu囧",
					    Toast.LENGTH_SHORT).show();
			}else{
				Intent mIntent = new Intent();  
		        mIntent.putExtra("widgetType",msg.what);
		        mIntent.putExtra("widgetName",nameEdit.getText().toString().trim()); 
		        // 设置结果，并进行传送  
		        setResult(0, mIntent);
		        finish();
			}
		}
	};
	private boolean isNameExist(){
		String name=nameEdit.getText().toString();
		for (Map.Entry<String, WidgetL> entry : ControlActivity.getMapInWidget().entrySet()) {
			   if(name.equals(entry.getKey())) return true;
		}
		for (Map.Entry<String, WidgetL> entry : ControlActivity.getMapOutWidget().entrySet()) {
			   if(name.equals(entry.getKey())) return true;
		}
		return false;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_widget, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
