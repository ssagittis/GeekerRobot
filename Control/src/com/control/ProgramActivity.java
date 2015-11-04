package com.control;


import java.util.Iterator;

import com.control.R;
import com.control.R.id;
import com.control.R.layout;
import com.control.R.menu;
import com.control.component.AbstrComponent;
import com.control.component.Component;
import com.control.component.InComponentBSensor;
import com.control.component.InComponentBWidget;
import com.control.component.OutComponentBSensor;
import com.control.component.OutComponentBWidget;
import com.control.component.IO.Input;
import com.control.component.IO.Output;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ProgramActivity extends Activity {
	public static final int RequestCodeOfAddComActivity=0;
	private RelativeLayout r;
	private Button add;
	private Button clear;
	private Button init;
	public static Component deletedCom;
	public final static String actionDeleteCertainCom="com.control.action.deleteCertainComToProgramActivity";
	private boolean deleteCondition=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_program);
		r= (RelativeLayout) this.findViewById(R.id.r);
		add= (Button) this.findViewById(R.id.add);
		clear= (Button) this.findViewById(R.id.clear);
		init= (Button) this.findViewById(R.id.init);
		add.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(ProgramActivity.this,AddComActivity.class); 
	            startActivityForResult(intent,RequestCodeOfAddComActivity);
			}
		});
		clear.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				/*deleteCondition=!deleteCondition;
				if(deleteCondition) clear.setBackground(ProgramActivity.this.getResources().getDrawable(R.drawable.clear_components_delete_condition));
				else clear.setBackground(ProgramActivity.this.getResources().getDrawable(R.drawable.clear_components));
				Component.setDeleteCondition(deleteCondition);*/
				Iterator<InComponentBSensor> iti=ComponentsService.setInComSen.iterator();
				while(iti.hasNext()){
					InComponentBSensor ics=iti.next();
					ics.delete();
					r.removeView(ics);
					iti.remove();
				}
				Iterator<OutComponentBSensor> ito=ComponentsService.setOutComSen.iterator();
				while(ito.hasNext()){
					OutComponentBSensor ics=ito.next();
					ics.delete();
					r.removeView(ics);
					ito.remove();
				}
				Iterator<AbstrComponent> ita=ComponentsService.setAbstrCom.iterator();
				while(ita.hasNext()){
					AbstrComponent ics=ita.next();
					ics.delete();
					r.removeView(ics);
					ita.remove();
				}
			}
				
		});
		init.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setAction(actionDeleteCertainCom); 
                sendBroadcast(intent);
			}
		});
		//注册广播收听器
		IntentFilter intentFilter = new IntentFilter();  
		intentFilter.addAction(NetSensorService.actionConnInfo); 
		        //intentFilter.addAction(NetSensorService.actionSenRenew);
		        //intentFilter.addAction(NetSensorService.actionSenRenewError);
		registerReceiver(new broadcastReciver(), intentFilter);
		/*SparseArray<Input> sain=new SparseArray<Input>();
        SparseArray<Input> sain2=new SparseArray<Input>();
        SparseArray<Output<?>> saout=new SparseArray<Output<?>>();
        sain2.append(0, new Input(DataType.N));
        sain.append(1, new Input(DataType.B));
        saout.append(0, new Output<Boolean>(DataType.B){
        	public Boolean getOutputData(){
        		return true;
        	}
        });
        saout.append(2, new Output<Integer>(DataType.N){
        	public Integer getOutputData(){
        		return 0;
        	}
        });
        OutComponentBSensor oc=new OutComponentBSensor(this,(new Point((float)150.0,(float)150.0)),3,sain);
        InComponentBSensor ic=new InComponentBSensor(this,(new Point((float)150.0,(float)700.0)),3,saout);
        OutComponentBSensor oc2=new OutComponentBSensor(this,(new Point((float)150.0,(float)150.0)),3,sain2);
        r.addView(oc);
        r.addView(ic);
        r.addView(oc2);*/
	}
	private class broadcastReciver extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context context, Intent intent) {
        	//要接收到广播必须要先注册！！！
        	Log.v("bear","deleteCom");
            String action = intent.getAction();  
            if (action.equals(ProgramActivity.actionDeleteCertainCom)) { 
                r.removeView(ProgramActivity.deletedCom); 
            }
        }  
  
    } 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.program, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		for(InComponentBSensor foo:ComponentsService.setInComSen){
			r.addView(foo);
		}
		for(InComponentBWidget foo:ComponentsService.setInComWidg){
			r.addView(foo);
		}
		for(OutComponentBSensor foo:ComponentsService.setOutComSen){
			r.addView(foo);
		}
		for(OutComponentBWidget foo:ComponentsService.setOutComWidg){
			r.addView(foo);
		}
		for(AbstrComponent foo:ComponentsService.setAbstrCom){
			r.addView(foo);
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		r.removeViews(3,r.getChildCount()-3);//页面被遮盖或销毁前把所有components回收
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
