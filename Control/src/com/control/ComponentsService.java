package com.control;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import com.control.component.AbstrComponent;
import com.control.component.InComponentBSensor;
import com.control.component.InComponentBWidget;
import com.control.component.OutComponentBSensor;
import com.control.component.OutComponentBWidget;
import com.control.component.IO.Input;
import com.control.component.IO.NoInputException;
import com.control.component.IO.Output;
import com.control.component.tools.Point;
import com.control.tools.DataType;
import com.control.tools.VideoData;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

public class ComponentsService extends Service{
	public static Set<InComponentBSensor> setInComSen=new HashSet<InComponentBSensor>();
	public static Set<InComponentBWidget> setInComWidg=new HashSet<InComponentBWidget>();
	public static Set<OutComponentBSensor> setOutComSen=new HashSet<OutComponentBSensor>();
	public static Set<OutComponentBWidget> setOutComWidg=new HashSet<OutComponentBWidget>();
	public static Set<AbstrComponent> setAbstrCom=new HashSet<AbstrComponent>();
	public final static String actionComRenew="com.control.action.comRenewToComponentsService";
	@Override 
    public void onCreate() {        
        super.onCreate(); 
        //以下注册接收广播
        IntentFilter intentFilter = new IntentFilter();  
        intentFilter.addAction(actionComRenew); 
		registerReceiver(new broadcastReciver(), intentFilter);
        /*OutComponentBSensor oc=new OutComponentBSensor(this,(new Point((float)150.0,(float)150.0)),3);
        InComponentBSensor ic=new InComponentBSensor(this,(new Point((float)150.0,(float)700.0)),3);
        OutComponentBSensor oc2=new OutComponentBSensor(this,(new Point((float)150.0,(float)150.0)),3);
        oc2.addInput(0, new Input(DataType.N));
        oc.addInput(1, new Input(DataType.B));
        ic.addOutput(0, new Output(DataType.B){
        	public Boolean getOutputData(){
        		return true;
        	}
        });
        ic.addOutput(2, new Output(DataType.N){
        	public Integer getOutputData(){
        		return 0;
        	}
        });
        setOutComSen.add(oc);
        setInComSen.add(ic);
        setOutComSen.add(oc2);*/
		/*InComponentBSensor ics=new InComponentBSensor(this,(new Point((float)150.0,(float)700.0)),1);
		ics.addOutput(0, new Output(DataType.V){

			@Override
			public Object getOutputData() throws NoInputException {
				
				return new VideoData();
			}

			@Override
			protected void unbindProcess() {
				
			}
        });
		setInComSen.add(ics);*/
    }  
    @Override 
    public void onDestroy() { 
        super.onDestroy(); 
    } 
  
    @Override 
    public int onStartCommand(Intent intent, int flags, int startId) { 
        return super.onStartCommand(intent, flags, startId); 
    } 
  
    @Override 
    public IBinder onBind(Intent arg0) {        
        return null; 
    } 
    private class broadcastReciver extends BroadcastReceiver {  
        @Override  
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();  
            if (action.equals(actionComRenew)) {  
               for(OutComponentBWidget o:setOutComWidg){
            	   try{
            		   o.getOutWidg().setInput(o.getInput().getOutputBind().getOutputData());
            	   }catch(NoInputException e){
            		   Toast.makeText(context, "有部分输出组件没有绑定输入组件",
       					    Toast.LENGTH_SHORT).show();
            	   }
               }
            }  
        }  
  
    }
   
}
