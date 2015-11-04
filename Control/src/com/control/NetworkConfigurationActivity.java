package com.control;

//import com.control.ControlActivity.ThreadTcpClient;

import com.control.tools.SharedPreferencesHelper;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NetworkConfigurationActivity extends Activity {
	private static final int SAVEIPSUCCESSFULLY = 0;
	private static final int SAVEIPFAILED = 1;
	private static final int CLEAN = 2;
	private static final int PORTFORMATEXCEPTION=3;
	private EditText IPInEdit;
	private EditText portInEdit;
	private EditText IPOutEdit;
	private EditText portOutEdit;
	private EditText IPVideoEdit;
	private EditText portVideoEdit;
	private Button save;
	private Button clean;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_configuration);
		//以下寻找控件
		IPInEdit = (EditText) this.findViewById(R.id.IPInEdit);
		portInEdit = (EditText) this.findViewById(R.id.portInEdit);
		IPOutEdit = (EditText) this.findViewById(R.id.IPOutEdit);
		portOutEdit = (EditText) this.findViewById(R.id.portOutEdit);
		IPVideoEdit = (EditText) this.findViewById(R.id.IPVideoEdit);
		portVideoEdit = (EditText) this.findViewById(R.id.portVideoEdit);
		save = (Button) this.findViewById(R.id.save);
		clean = (Button) this.findViewById(R.id.clean);
		//以下读出存储的IP和port，填入控件
		SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesHelper.NAME, 0);
		String IPIn = sharedPreferences.getString("IPIn",getResources().getString(R.string.IPInDefault));
		int portIn = sharedPreferences.getInt("portIn",getResources().getInteger(R.integer.portInDefault));
		String IPOut = sharedPreferences.getString("IPOut",getResources().getString(R.string.IPOutDefault));
		int portOut = sharedPreferences.getInt("portOut",getResources().getInteger(R.integer.portOutDefault));
		String IPVideo = sharedPreferences.getString("IPVideo",getResources().getString(R.string.IPVideoDefault));
		int portVideo = sharedPreferences.getInt("portVideo",getResources().getInteger(R.integer.portVideoDefault));
		IPInEdit.setText(IPIn);
		portInEdit.setText(portIn+"");
		IPOutEdit.setText(IPOut);
		portOutEdit.setText(portOut+"");
		IPVideoEdit.setText(IPVideo);
		portVideoEdit.setText(portVideo+"");
		//以下绑定监听器
		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {//保存IP和port
				try{
					SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesHelper.NAME, 0);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putInt("portIn",Integer.parseInt(portInEdit.getText().toString()));
					editor.putInt("portOut",Integer.parseInt(portOutEdit.getText().toString()));
					editor.putInt("portVideo",Integer.parseInt(portVideoEdit.getText().toString()));
					Message msg = new Message();
					if(isIP(IPInEdit.getText().toString())||
							isIP(IPOutEdit.getText().toString())||
							isIP(IPVideoEdit.getText().toString())){
						editor.putString("IPIn",IPInEdit.getText().toString());
						editor.putString("IPOut",IPOutEdit.getText().toString());
						editor.putString("IPVideo",IPVideoEdit.getText().toString());
						msg.what = SAVEIPSUCCESSFULLY;
					}else{
						msg.what = SAVEIPFAILED;
					}
					mHandler.sendMessage(msg);
					editor.commit();
				}catch(NumberFormatException e){
					Message msg = new Message();
					msg.what = PORTFORMATEXCEPTION;
					mHandler.sendMessage(msg);
				}finally{
					
				}
			}
			private String trimSpaces(String IP){//去掉IP字符串前后所有的空格  
		        while(IP.startsWith(" ")){  
		               IP= IP.substring(1,IP.length()).trim();  
		            }  
		        while(IP.endsWith(" ")){  
		               IP= IP.substring(0,IP.length()-1).trim();  
		            }  
		        return IP;  
		    }  
			private boolean isIP(String IP){//判断是否是一个IP  
		        boolean b = false;  
		        IP = trimSpaces(IP);  
		        if(IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")){  
		            String s[] = IP.split("\\.");  
		            if(Integer.parseInt(s[0])<255)  
		                if(Integer.parseInt(s[1])<255)  
		                    if(Integer.parseInt(s[2])<255)  
		                        if(Integer.parseInt(s[3])<255)  
		                            b = true;  
		        }  
		        return b;  
		    }
		});
		clean.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {//保存IP和port
				Message msg = new Message();
				msg.what = CLEAN;
				mHandler.sendMessage(msg);
			}
		});
	}
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast toast;
			switch(msg.what){
			case SAVEIPSUCCESSFULLY:
				toast=Toast.makeText(getApplicationContext(), "IP和port保存成功",
    				    Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
	    		toast.show();
				break;
			case SAVEIPFAILED:
				toast=Toast.makeText(getApplicationContext(), "IP保存失败，port保存成功",
    				    Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
	    		toast.show();
				break;
			case CLEAN:
				IPInEdit.setText("");
				portInEdit.setText("");
				IPOutEdit.setText("");
				portOutEdit.setText("");
				IPVideoEdit.setText("");
				portVideoEdit.setText("");
				break;
			case PORTFORMATEXCEPTION:
				toast=Toast.makeText(getApplicationContext(), "port为空，保存失败",
    				    Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
	    		toast.show();
				break;
			}
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.network_configuration, menu);
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
