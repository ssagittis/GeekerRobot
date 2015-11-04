package com.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.control.component.Component;
import com.control.component.InComponentBWidget;
import com.control.component.OutComponentBWidget;
import com.control.tools.SharedPreferencesHelper;
import com.control.widget.BTextViewWidg;
import com.control.widget.CheckBoxWidg;
import com.control.widget.EditTextWidg;
import com.control.widget.InWidget;
import com.control.widget.NTextViewWidg;
import com.control.widget.OutWidget;
import com.control.widget.SeekBarWidg;
import com.control.widget.VideoButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ControlActivity extends Activity {
	//private SensorState senState=SensorState.getInstance();
	//private static final int SENRENEW=4;//因为0、1、2、3直接用NetSensorService的常量了，所以只能从4开始用
	//private static final int SENRENEWERROR=5;
	public static final boolean STARTTHREAD=true;
	public static final boolean STOPTHREAD=false;
	private static final int RequestCodeOfAddWidgetActivity=0;
	public final static String actionConnInfo="com.control.action.connInfoToNetSensorService";
	//public final static String actionActivateNetOutputInfo="com.control.action.activateNetOutputToNetSensorService";
	//public final static String actionInactivate="com.control.action.inactivateToNetSensorService";
	private Button conn;
	private Button config;
	//private Button activate;
	private Button program;
	private Button addWidget;
	private Button deleteWidget;
	private LinearLayout widgetLL;
	private LinearLayout widgetLR;
	private Context context=this;
	
	private static boolean deleteStateFlag=false;
	private static Map<String,WidgetL> mapInWidget=new HashMap<String,WidgetL>();
	private static Map<String,WidgetL> mapOutWidget=new HashMap<String,WidgetL>();	
	public static Map<String, WidgetL> getMapInWidget() {
		return mapInWidget;
	}
	public static Map<String, WidgetL> getMapOutWidget() {
		return mapOutWidget;
	}
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
		setOnclick();
	}

	private void init() {
		conn = (Button) this.findViewById(R.id.conn);
		config = (Button) this.findViewById(R.id.config);
		//activate = (Button) this.findViewById(R.id.activate);
		program = (Button) this.findViewById(R.id.program);
		addWidget= (Button) this.findViewById(R.id.addWidget);
		deleteWidget= (Button) this.findViewById(R.id.deleteWidget);
		widgetLL=(LinearLayout)this.findViewById(R.id.widgetLL);
		widgetLR=(LinearLayout)this.findViewById(R.id.widgetLR);
		
		
		//启动负责管理网络连接与传感器管理的Service与负责管理编程组件的Service
		Intent i = new Intent(ControlActivity.this, NetSensorService.class); 
        ControlActivity.this.startService(i);  
        Intent i2= new Intent(ControlActivity.this, ComponentsService.class); 
        ControlActivity.this.startService(i2);  
		//注册广播收听器
		IntentFilter intentFilter = new IntentFilter();  
        intentFilter.addAction(NetSensorService.actionConnInfo); 
        //intentFilter.addAction(NetSensorService.actionSenRenew);
        //intentFilter.addAction(NetSensorService.actionSenRenewError);
		registerReceiver(new broadcastReciver(), intentFilter);
		
		//widgetLL.removeView(widgetL1);
		//sensorLinearLayout.removeAllViews();
		/*Button btn1 = new Button(this);
		btn1.setText("123");
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5, 5, 5, 5);
        btn1.setLayoutParams(lp); 
		btn1.setBackground(this.getResources().getDrawable(R.drawable.sensor_state_button));
		//sensorLinearLayout.addView(btn1);
		
		btn1 = new Button(this);
		btn1.setText("123");
		lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5, 5, 5, 5);
        btn1.setLayoutParams(lp); 
		btn1.setBackground(this.getResources().getDrawable(R.drawable.sensor_state_button));
		//sensorLinearLayout.addView(btn1);*/
		
	}
	//private static boolean deleteStateFlag=false;
	private void setOnclick() {
		conn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				Message msg = new Message();//新建Message类对象msg
				if(NetSensorService.getConnState()==NetSensorService.DISCONNECTED){
					//以下通知NetSensorService开始连接网络 
    	            intent.setAction(actionConnInfo); 
    	            intent.putExtra("connInfo", STARTTHREAD);
    	            //以下通知Handler改变UI
	                msg.what = NetSensorService.CONNECTING;
				}else if(NetSensorService.getConnState()==NetSensorService.CONNECTING){
					//以下通知NetSensorService取消连接网络 
    	            intent.setAction(actionConnInfo); 
    	            intent.putExtra("connInfo", STOPTHREAD);
    	            //以下通知Handler改变UI
	                msg.what = NetSensorService.DISCONNECTED;
				}else if(NetSensorService.getConnState()==NetSensorService.CONNECTED){
					//以下通知NetSensorService取消连接网络 
    	            intent.setAction(actionConnInfo); 
    	            intent.putExtra("connInfo", STOPTHREAD); 
    	            //以下通知Handler改变UI
	                msg.what = NetSensorService.DISCONNECTED;
				}else if(NetSensorService.getConnState()==NetSensorService.FAILED){
					//以下通知NetSensorService取消连接网络 
    	            intent.setAction(actionConnInfo); 
    	            intent.putExtra("connInfo", STARTTHREAD); 
    	            //以下通知Handler改变UI
	                msg.what = NetSensorService.CONNECTING;
				}
				ControlActivity.this.sendBroadcast(intent);
				mHandler.sendMessage(msg);
			}
		});
		/*activate.setOnClickListener(new Button.OnClickListener(){
			private boolean isActive=false;//是否向校小车输出初始化信息和控制信息
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				if(isActive){
					activate.setText("启用");
					activate.setTextColor(Color.BLACK);
					isActive=!isActive;
					program.setVisibility(Button.VISIBLE);
					intent.setAction(actionActivateNetOutputInfo); 
    	            intent.putExtra("activateNetOutput", true);
	                ControlActivity.this.sendBroadcast(intent);
				}else{
					activate.setText("取消启用");
					activate.setTextColor(Color.RED);
					isActive=!isActive;
					program.setVisibility(Button.INVISIBLE);
					intent.setAction(actionActivateNetOutputInfo); 
    	            intent.putExtra("activateNetOutput", false);
	                ControlActivity.this.sendBroadcast(intent);
				}
			}
		});*/
		config.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(ControlActivity.this,NetworkConfigurationActivity.class); 
	            startActivity(intent);
			}
		});
		deleteWidget.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				deleteStateFlag=!deleteStateFlag;
				if(deleteStateFlag){
					for (Map.Entry<String, WidgetL> entry : mapInWidget.entrySet()) {
						   entry.getValue().changeToDeleteState();
					}
					for (Map.Entry<String, WidgetL> entry : mapOutWidget.entrySet()) {
						   entry.getValue().changeToDeleteState();
					}
					deleteWidget.setText("退出删除状态");
					deleteWidget.setTextColor(Color.RED);
				}else{
					for (Map.Entry<String, WidgetL> entry : mapInWidget.entrySet()) {
						   entry.getValue().changeToNormalState();
					}
					for (Map.Entry<String, WidgetL> entry : mapOutWidget.entrySet()) {
						   entry.getValue().changeToNormalState();
					}
					deleteWidget.setText("删除控件");
					deleteWidget.setTextColor(Color.BLACK);
				}
			}
		});
		addWidget.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(ControlActivity.this,AddWidgetActivity.class); 
	            startActivityForResult(intent,RequestCodeOfAddWidgetActivity);
			}
		});
		program.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(ControlActivity.this,ProgramActivity.class); 
	            startActivity(intent);
			}
		});
	}

//点击返回键时的操作
	private class broadcastReciver extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context context, Intent intent) {
        	//要接收到广播必须要先注册！！！
        	Message msg = new Message();//新建Message类对象msg
            String action = intent.getAction();  
            if (action.equals(NetSensorService.actionConnInfo)) {  
                boolean actionConnInfo= intent.getBooleanExtra("connInfo", NetSensorService.CONNFAILED);  
                if(actionConnInfo==NetSensorService.CONNSUCCESSFULLY) msg.what = NetSensorService.CONNECTED;
                else msg.what = NetSensorService.FAILED;
				mHandler.sendMessage(msg);
                //ControlActivity.this.unregisterReceiver(this);  
            }/*else if(action.equals(NetSensorService.actionSenRenew)){
            	msg.what = SENRENEW;
				mHandler.sendMessage(msg);
            }else if(action.equals(NetSensorService.actionSenRenewError)){
            	Log.v("bear","bearRenewError");
            	msg.what = SENRENEWERROR;
				mHandler.sendMessage(msg);
            }*/
        }  
  
    }  
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("退出？");
			builder.setMessage("退出？");
			builder.setPositiveButton("是",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface diaconsole, int which) {
							
							finish();//退出应用
						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface diaconsole, int which) {

						}
					});
			builder.show();
		}
		return super.onKeyDown(keyCode, event);
	}

	
	

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NetSensorService.DISCONNECTED:
				conn.setText("建立连接");
				conn.setTextColor(Color.BLACK);
				//program.setVisibility(Button.VISIBLE);
				break;
			case NetSensorService.CONNECTING:
				conn.setText("正在连接，请稍后……点击取消连接");
				conn.setTextColor(Color.RED);
				//program.setVisibility(Button.INVISIBLE);
				break;
			case NetSensorService.CONNECTED:
				conn.setText("连接成功，点击取消连接");
				conn.setTextColor(Color.BLUE);
				//program.setVisibility(Button.INVISIBLE);
				break;
			case NetSensorService.FAILED:
				conn.setText("连接失败，点击重新建立连接");
				conn.setTextColor(Color.BLACK);
				//program.setVisibility(Button.VISIBLE);
				break;
			//以上跟ID为conn的Button有关
			}
		}
	};
	/** 
     * 复写onActivityResult，这个方法 
     * 是要等到SimpleTaskActivity点了提交过后才会执行的 
      */
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
        super.onActivityResult(requestCode, resultCode, data); 
        LinearLayout widgetAdded=(widgetLL.getChildCount()>widgetLR.getChildCount())?widgetLR:widgetLL;
        if(requestCode==RequestCodeOfAddWidgetActivity){
        	if(data!=null){
	        	int widgetType=data.getIntExtra("widgetType", Integer.MAX_VALUE);
	        	String widgetName=data.getStringExtra("widgetName");
	        	WidgetL w=null;
	        	switch(widgetType){
	        	case 0:
		        	{
	        		CheckBoxWidg cb = new CheckBoxWidg(this);
	        		cb.setGravity(Gravity.CENTER);
	        		w = new WidgetL(this,cb,widgetName);
	        		mapInWidget.put(widgetName, w);
	        		//w.changeToDeleteState();
	        		widgetAdded.addView(w);
	        		InComponentBWidget ic=new InComponentBWidget(context,
							w.getInWidget(),widgetName);
					ComponentsService.setInComWidg.add(ic);	
					w.setCom(ic);
	        		break;}
	        	case 1:
		        	{
	        		EditTextWidg et = new EditTextWidg(this);
	        		w = new WidgetL(this,et,widgetName);
	        		mapInWidget.put(widgetName, w);
	        		widgetAdded.addView(w);
	        		InComponentBWidget ic=new InComponentBWidget(context,
							w.getInWidget(),widgetName);
					ComponentsService.setInComWidg.add(ic);
					w.setCom(ic);
	        		break;}
	        	case 2:
	        		{
	        		SeekBarWidg sb= new SeekBarWidg(context);
	        		w = new WidgetL(this,sb,widgetName);
	        		mapInWidget.put(widgetName, w);
	        		widgetAdded.addView(w);
	        		InComponentBWidget ic=new InComponentBWidget(context,
							w.getInWidget(),widgetName);
		        	ComponentsService.setInComWidg.add(ic);
		        	w.setCom(ic);
	        		break;}
	        	case 3:
	        		{
	        		NTextViewWidg ntv= new NTextViewWidg(this);
	        		w = new WidgetL(this,ntv,widgetName);
	        		mapOutWidget.put(widgetName, w);
	        		widgetAdded.addView(w);
	        		OutComponentBWidget oc=new OutComponentBWidget(context,
							w.getOutWidget(),widgetName);
		        	ComponentsService.setOutComWidg.add(oc);
		        	w.setCom(oc);
	        		break;}
	        	case 4:
		        	{
	        		BTextViewWidg btv= new BTextViewWidg(this);
	        		w = new WidgetL(this,btv,widgetName);
	        		mapOutWidget.put(widgetName, w);
	        		widgetAdded.addView(w);
	        		OutComponentBWidget oc=new OutComponentBWidget(context,
							w.getOutWidget(),widgetName);
					ComponentsService.setOutComWidg.add(oc);
					w.setCom(oc);
	        		break;}
	        	case 5:
		        	{
	        		VideoButton btv= new VideoButton(this);
	        		w = new WidgetL(this,btv,widgetName);
	        		mapOutWidget.put(widgetName, w);
	        		widgetAdded.addView(w);
	        		OutComponentBWidget oc=new OutComponentBWidget(context,
							w.getOutWidget(),widgetName);
					ComponentsService.setOutComWidg.add(oc);
					w.setCom(oc);
	        		break;}
	        	default:
	        	}
	        	if(deleteStateFlag) w.changeToDeleteState();
        	}
        }
    } 
	public class WidgetL extends LinearLayout{
		private Component com=null;
		public void setCom(Component com) {
			this.com = com;
		}
		public Component getCom() {
			return com;
		}
		private Button btnWidgetDelete;
		private View viewAdded;
		public InWidget getInWidget() {
			return (InWidget)viewAdded;
		}
		public OutWidget getOutWidget() {
			return (OutWidget)viewAdded;
		}
		public View getWidget() {
			return viewAdded;
		}
		private final String name;
		public WidgetL(Context context,View viewAdded,String widgetName) {
			//各种初始化
			super(context);
			this.setOrientation(VERTICAL);
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = mInflater.inflate(R.layout.widget, null);
			this.addView(v);
			//用户控件的名字显示出来
			TextView tvWidgetName = (TextView)findViewById(R.id.widgetName);
			tvWidgetName.setText(widgetName);
			this.name=widgetName;
			//把用户控件加进来
			LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	        lp.setMargins(5, 5, 5, 5);
	        viewAdded.setLayoutParams(lp);
			addView(viewAdded);
			this.viewAdded=viewAdded;
			//设置删除按键
			btnWidgetDelete=(Button)findViewById(R.id.widgetDelete);
			btnWidgetDelete.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					widgetLL.removeView(mapInWidget.get(name));
					widgetLL.removeView(mapOutWidget.get(name));
					widgetLR.removeView(mapInWidget.get(name));
					widgetLR.removeView(mapOutWidget.get(name));
					
					ComponentsService.setOutComWidg.remove(com);
					
					ComponentsService.setInComWidg.remove(com);
					com.delete();
					mapInWidget.remove(name);
					mapOutWidget.remove(name);
				}
			});
		}
		/*public WidgetL(Context context, AttributeSet attrs) {
			super(context, attrs);
			 
		}*/
		@Override
		public void setLayoutParams(android.view.ViewGroup.LayoutParams params) {
			//参数设置不合理，显示效果很差
			params.width=LinearLayout.LayoutParams.MATCH_PARENT;
			params.height=LinearLayout.LayoutParams.WRAP_CONTENT;
			super.setLayoutParams(params);
		}
		public void changeToDeleteState(){
			btnWidgetDelete.setVisibility(VISIBLE);
			viewAdded.setVisibility(GONE);
		}
		public void changeToNormalState(){
			btnWidgetDelete.setVisibility(GONE);
			viewAdded.setVisibility(VISIBLE);
		}
	}
}