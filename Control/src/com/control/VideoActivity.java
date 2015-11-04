package com.control;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import com.control.tools.SharedPreferencesHelper;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class VideoActivity extends Activity {
	private ImageView imageView = null;    
    private Bitmap bmp = null;  
    private Socket socket = null;  
    private Handler h = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			imageView.setImageBitmap(bmp); 
		}
    	
    }; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);   
    
        imageView = (ImageView) findViewById(R.id.image01); 
        
        Thread t=new Thread(){

			@Override
			public void run() {
				super.run();
				try{   
		            while(true){
		            	SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesHelper.NAME, 0);
		    			String IP = sharedPreferences.getString("IPVideo",getResources().getString(R.string.IPVideoDefault));
		    			int port = sharedPreferences.getInt("portVideo",getResources().getInteger(R.integer.portVideoDefault));
		    			
		            	socket = new Socket(IP, port); 
			            DataInputStream dataInput = new DataInputStream(socket.getInputStream());  
			                    
				        int size = dataInput.readInt(); 
				        //Log.v("bear","size"+size);
				        byte[] data = new byte[0x8000];    
				        int len = 0; int n;   
				        while(true){
				        	n=dataInput.read(data,len,0x8000-len);
				        	Log.v("bear","size"+n);
				        	if(n<=0)break;
				        	else
				        		len+=n;
				        }
				        Log.v("bear","total"+len);
				        //while (len < size) len += dataInput.read(data, len, size - len);    
				        ByteArrayOutputStream outPut = new ByteArrayOutputStream();    
				        bmp = BitmapFactory.decodeByteArray(data, 0, data.length);    
				        bmp.compress(CompressFormat.PNG, 100, outPut);  
				        Message m=new Message();
				        m.what=0;
				        h.sendMessage(m);
				        socket.close();  
		            }
		        } catch (IOException e) {    
		        	e.printStackTrace();    
		        }
			}
		}; 
		t.start(); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video, menu);
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
