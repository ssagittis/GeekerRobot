package com.control;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import com.control.component.InComponentBSensor;
import com.control.component.OutComponentBSensor;
import com.control.component.IO.NoInputException;
import com.control.tools.SharedPreferencesHelper;
import com.control.tools.T;
import com.control.tools.sensor.Sensor;
import com.control.tools.sensor.SensorIn;
import com.control.tools.sensor.SensorOut;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class NetSensorService extends Service{//负责管理socket长连接和存储传感器状态的Service
	//private SensorState senState=SensorState.getInstance();
	//public SensorState getSenState() {
		//return senState;
	//}
	private InThread inThread;
	private Socket socketOut;
	private Socket socketIn;
	private Context context=this;
	InputStream inputStream;
	OutputStream outputStream;
	public static final boolean CONNSUCCESSFULLY=true;
	public static final boolean CONNFAILED=false;
	
	public static final int DISCONNECTED=0;
	public static final int CONNECTING=1;
	public static final int CONNECTED=2;
	public static final int FAILED=3;
	private static int connState=DISCONNECTED;
	public static int getConnState() {
		return connState;
	}
	
	private boolean stopInThread=true;
	private boolean isActivateNetOutput=false;
	public final static String actionConnInfo="com.control.action.connInfoToControlActivity";
	public final static String actionSenInRenew="com.control.action.senInRenewToNetSensorService";
	public final static String actionSenRenewError="com.control.action.senInRenewErrorToNetSensorService";
	public final static String actionSenOutRenew="com.control.action.senOutRenewToNetSensorService";
	public final static String actionSendInitialization="com.control.action.sendInitializationToNetSensorService";
	
	@Override 
    public void onCreate() {        
        super.onCreate(); 
        //wifiThread.start();
        IntentFilter intentFilter = new IntentFilter();  
        intentFilter.addAction(ControlActivity.actionConnInfo); 
        intentFilter.addAction(actionSenOutRenew);
        intentFilter.addAction(actionSendInitialization);
		registerReceiver(new broadcastReciver(), intentFilter);
    } 
  
    @Override 
    public void onDestroy() { 
        super.onDestroy(); 
        stopInThread=true;
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
            if (action.equals(ControlActivity.actionConnInfo)) {  
                boolean actionConnInfo= intent.getBooleanExtra("connInfo", ControlActivity.STOPTHREAD);  
                if(actionConnInfo==ControlActivity.STARTTHREAD){
                	connState=CONNECTING;
                	stopInThread=false;
                	inThread=new InThread();
                	inThread.start();
                }else{
                	connState=DISCONNECTED;
                	stopInThread=true;
                	inThread=null;
                }
            }else if (action.equals(actionSenOutRenew)) {
                sensorOutRenew();    
            }else if (action.equals(actionSendInitialization)) {
                try {
					sendInitialization();
				} catch (IOException e) {
					e.printStackTrace();
				}
                  
            }
        }  
  
    }
    private void sensorOutRenew(){
    	if(getConnState()==CONNECTED){
    		Log.v("bear","sensorOutRenew");
			int sensorOutNum=ComponentsService.setOutComSen.size();
			Vector<Integer> vi=new Vector<Integer>();
			vi.add(0xf8f8);
			vi.add(0);
			vi.add(0x0);
			vi.add(sensorOutNum);
			Log.v("bear","sensorOutNum:"+sensorOutNum);
			for(OutComponentBSensor ocs:ComponentsService.setOutComSen){
				vi.add(ocs.getSensorID());
				//vi.add(ocs.getSensor().getParaNum());
				Log.v("bear","ParaNum:"+ocs.getSensor().getParaNum());
				for(int i=0;i<8;i++){
					try{
						vi.add((Integer)ocs.getSArrayInputPos().valueAt(i).getOutputBind().getOutputData());
					}/*catch(NoInputException e){
						vi.add(0);
					}*/
					catch(Exception e){
						vi.add(0);
					}
				}
			}
			vi.set(1,vi.size()*4-16);
			Log.v("bear","OutSize:"+(vi.size()*4-16));
			for(int i=0;i<vi.size();i++){
				Log.v("bear","out:"+vi.elementAt(i));
			}
			int[] out=new int[vi.size()];
			for(int i=0;i<vi.size();i++){
				out[i]=vi.elementAt(i);
			}
			try {
				outputStream.write(int2byte(out));
				outputStream.flush();
			} catch (IOException e) {
			}
    	}
	}
    public void sendInitialization() throws IOException{
    	if(getConnState()==CONNECTED){
	    	Log.v("bear","initOutput begin");
			int sensorOutNum=ComponentsService.setOutComSen.size();
			int sensorInNum=ComponentsService.setInComSen.size();
			int sensorNum=sensorOutNum+sensorInNum;
			int[] bufferOut=new int[4+sensorNum*9];
			bufferOut[0]=0xf8f8;
			bufferOut[1]=sensorNum*9*4;
			bufferOut[2]=0x01;
			bufferOut[3]=sensorNum;
			int j=0;
			for(OutComponentBSensor os:ComponentsService.setOutComSen){
				bufferOut[4+j*9]=os.getSensorID();
				for(int i=0;i<Sensor.INITPARASNUM;i++){
					bufferOut[5+j*9+i]=os.getSensor().getInitParas(i);
				}
				j++;
			}
			for(InComponentBSensor is:ComponentsService.setInComSen){
				bufferOut[4+j*9]=is.getSensorID();
				for(int i=0;i<Sensor.INITPARASNUM;i++){
					bufferOut[5+j*9+i]=is.getSensor().getInitParas(i);
				}
				j++;
			}
			outputStream.write(int2byte(bufferOut));
			outputStream.flush();
			for(int i=0;i<bufferOut.length;i++){
				Log.v("bear",bufferOut[i]+"");
			}
			Log.v("bear","initOutput end");
    	}
    }
    private byte[] int2byte(int[] data)
    {
        byte[] bytes = new byte[4*data.length];
        for(int i=0;i<data.length;i++){
        	bytes[0+i*4] = (byte) (data[i] & 0xff);
            bytes[1+i*4] = (byte) ((data[i] & 0xff00) >> 8);
            bytes[2+i*4] = (byte) ((data[i] & 0xff0000) >> 16);
            bytes[3+i*4] = (byte) ((data[i] & 0xff000000) >> 24);
        }
        return bytes;
    }
	private int[] byte2int(byte[] data)
    {
        int[] ints = new int[data.length/4];
        for(int i=0;i<ints.length;i++){
            ints[i] =(0xff & data[0+i*4]) | 
            (0xff00 & (data[1+i*4] << 8)) | 
            (0xff0000 & (data[2+i*4] << 16)) | 
            (0xff000000 & (data[3+i*4] << 24));
    	}
        return ints;
    }
    private class InThread extends Thread{
    	public void socketInit()throws IOException{
    		try {
    			SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesHelper.NAME, 0);
    			String IPOut = sharedPreferences.getString("IPOut",getResources().getString(R.string.IPOutDefault));
    			int portOut = sharedPreferences.getInt("portOut",getResources().getInteger(R.integer.portOutDefault));
    			//servSock=new ServerSocket(port);
    			//servSock.setSoTimeout(6000);
    			//socket = servSock.accept();
    			socketOut = new Socket(IPOut, portOut);
    			Log.v("bear","bearSocketCreateedSucc");
    			if(socketOut.isConnected()){
    				System.out.println("socket sus!");
    				//以下更改状态
    				connState=CONNECTED;
    				//以下通知ControlActivity改变UI
    				Intent intent = new Intent();  
    	            intent.setAction(actionConnInfo); 
    	            intent.putExtra("connInfo", CONNSUCCESSFULLY); 
    	            NetSensorService.this.sendBroadcast(intent);
    	            //以下保证线程运行不终止
    	            stopInThread=false;
    	            //以下获得输入输出流
    	            //inputStream = socketOut.getInputStream();
    	            outputStream = socketOut.getOutputStream();
    			}
    			String IPIn = sharedPreferences.getString("IPIn",getResources().getString(R.string.IPInDefault));
    			int portIn = sharedPreferences.getInt("portIn",getResources().getInteger(R.integer.portInDefault));
    			socketIn = new Socket(IPIn, portIn);
    			inputStream = socketIn.getInputStream();
    			/*else{
    				System.out.println("socket failed!");
    				connState=FAILED;
    				Intent intent = new Intent();  
    	            intent.setAction(actionConnInfo); 
    	            intent.putExtra("connInfo", CONNFAILED); 
    	            NetSensorService.this.sendBroadcast(intent);
    			}*/
    		}catch (UnknownHostException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	@Override
    	public void run() {
    		try{
    			Log.v("bear","thread begin");
    			socketInit();
    			if(!stopInThread){
    				sendInitialization();
    			}
	    		while(!stopInThread){
	    			byte buffer[] = new byte[1024*4];	
	    			int tempsize = inputStream.read(buffer);//如果没有数据会被阻塞
	    			//Log.v("bear","tempSize"+tempsize);
	    			if(tempsize<=0) continue;
	    			else{
	    				if(!decodeAndSave(this.byte2int(buffer,tempsize/4))){
	    					//Intent intent = new Intent();  
	    		            //intent.setAction(actionSenRenewError);
	    		            //NetSensorService.this.sendBroadcast(intent);
	    					Log.v("bear","解码失败");
	    					continue;
	    				}else{
	    					Log.v("bear","解码成功");
	    					Intent intent = new Intent();  
	    		            intent.setAction(ComponentsService.actionComRenew);
	    		            NetSensorService.this.sendBroadcast(intent);
	    					
	    					//sensorOutRenew();
	    				}
	    			}
	    		}
    		}catch (Exception e) {
    			connState=FAILED;
    			Intent intent = new Intent();  
	            intent.setAction(actionConnInfo); 
	            intent.putExtra("connInfo", CONNFAILED); 
	            NetSensorService.this.sendBroadcast(intent);
	            stopInThread=true;
	            e.printStackTrace();
	            Log.v("bear","thread Exception end");
    		}finally{
    			Log.v("bear","thread end");
    		}
    	}
    	private boolean decodeAndSave(int[] buffer){
    		boolean out=true;
    		try{
    			//String str="bearBuffer:";
    			for(int i=0;i<buffer.length;i++){
    				Log.v("bear","buffer["+i+"]:"+buffer[i]);
    			}
    			//Log.v("bear",str);
    			//if(buffer[0]!=0xf8f8) throw new NoEmptyException();
    			Log.v("bear","bear1");
    			//int bagSize = getint(buffer,0);
    			/*Log.v("bear","buffer[0]:"+buffer[0]);
    			Log.v("bear","buffer[1]:"+buffer[1]);
    			Log.v("bear","buffer[2]:"+buffer[2]);
    			Log.v("bear","buffer[3]:"+buffer[3]);
    			Log.v("bear","buffer[4]:"+buffer[4]);
    			Log.v("bear","buffer[5]:"+buffer[5]);
    			Log.v("bear","buffer[6]:"+buffer[6]);*/
    			Log.v("bear","buffer.length:"+buffer.length);
				//if(buffer[1]!=(buffer.length-4)*4) throw new NoEmptyException();
				Log.v("bear","bear12");
				//int cnum = getint(buffer,4);
				//if(buffer[2]!=0x02) throw new NoEmptyException();
				Log.v("bear","bear123");
				if(buffer[3]<0) throw new NoEmptyException();
				int sensorInNum=buffer[3];
				//SensorIn.empty();
				//ComponentsService.setInComSen.clear();
				Log.v("bear","bear456");
				int bufferRead=4;
				for(int i=0;i<sensorInNum;i++){
					int sensorID = buffer[bufferRead];
					//int paranum = buffer[bufferRead+1];
					//if(paranum<0){
					//	paranum=0;
					//}
					//if(paranum>0){
						//Log.v("bear","paranum="+paranum);
						Log.v("bear","ComponentsService.setInComSenSize:"+ComponentsService.setInComSen.size());
						for(InComponentBSensor is:ComponentsService.setInComSen){
							Log.v("bear","is.getSensorID()="+is.getSensorID());
							if(is.getSensorID()==sensorID){
								for(int j=0;j<is.getSensor().getParaNum();j++){
									is.getSensor().setPara(j, buffer[bufferRead+1+j]);
									Log.v("bear","setPara("+j+","+buffer[bufferRead+1+j]+")");
								}
							}
						}
					//}
					bufferRead+=1+8;
				}
				//以下调试输出接收到的传感器信息
				/*Log.v("bear","bearSenStateSize:"+SensorIn.getSensorNum());
				for(int k=0;k<SensorIn.getSensorNum();k++){
					Log.v("bear","bearSen:"+k);
					for(int l=0;l<SensorIn.getSensor(k).getParaNum();l++){
						Log.v("bear","bearSenPara:"+SensorIn.getSensor(k).getPara(l));
					}
				}*/
				//Writer writer = new OutputStreamWriter(outputStream);  
			    //writer.write("Hello Client.");  
			    //writer.flush();
			    //Log.v("bear","bearOutputStreamSend");
    		}catch(NoEmptyException e){
    			out=false;
    			Log.e("bear","bearNoEmptyException");
    		}catch(Exception e){
    			//SensorIn.empty();
    			out=false;
    			Log.e("bear","bearException");
    		}
    		return out;
    	}
    	private int getint(byte[] bytes,int offset){
    		return (0xff & bytes[3+offset])|(0xff00&(bytes[2+offset]<<8))|(0xff0000&(bytes[1+offset]<<16))|(0xff000000&(bytes[0+offset]<<24)); 
    	}
    	
    	private int[] byte2int(byte[] data,int size)
        {
            int[] ints = new int[size];
            for(int i=0;i<ints.length;i++){
                ints[i] =(0xff & data[0+i*4]) | 
                (0xff00 & (data[1+i*4] << 8)) | 
                (0xff0000 & (data[2+i*4] << 16)) | 
                (0xff000000 & (data[3+i*4] << 24));
        	}
            return ints;
        }
    	
    	private class NoEmptyException extends Exception{
			private static final long serialVersionUID = 1L;
			NoEmptyException(){
				super("NoEmptyException");
			}
    	}
    }
    
}
