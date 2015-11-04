package com.control;

import java.util.Map;
import java.util.Random;
import java.util.Vector;

import com.control.ControlActivity.WidgetL;
import com.control.R;
import com.control.R.id;
import com.control.R.layout;
import com.control.R.menu;
import com.control.component.AbstrComponent;
import com.control.component.InComponentBSensor;
import com.control.component.InComponentBWidget;
import com.control.component.OutComponentBSensor;
import com.control.component.OutComponentBWidget;
import com.control.component.IO.Input;
import com.control.component.IO.NoInputException;
import com.control.component.IO.Output;
import com.control.component.abstrComponent.*;
import com.control.component.tools.Point;
import com.control.tools.DataType;
import com.control.tools.onlyCheckBox.OnlyCheckBox;
import com.control.tools.onlyCheckBox.OnlyCheckBoxHelper;
import com.control.tools.sensor.Sensor;
import com.control.widget.InWidget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddComActivity extends Activity {
	private Context context=this;
	private static int nextSensorOutID=0;
	private static int nextSensorInID=0;
	private static final int BSHUNT=0;
	private static final int NSHUNT=1;
	private static final int MULTIPLICATION=2;
	private static final int GREATER=3;
	private static final int LESS=4;
	private static final int NCONSTANT=5;
	private static final int BCONSTANT=6;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_com);
		Button addInCom=(Button)findViewById(R.id.addInCom);
		Button addOutCom=(Button)findViewById(R.id.addOutCom);
		Button addAbstrCom=(Button)findViewById(R.id.addAbstrCom);
		addInCom.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				setContentView(R.layout.activity_add_in_com_bind_sensor);
				Button add=(Button)findViewById(R.id.add);
				//final EditText IDEdit=(EditText)findViewById(R.id.IDEdit);
				final EditText outputPosEdit=(EditText)findViewById(R.id.outputPosEdit);
				final EditText[] paraEdits=findParaEdit();
				add.setOnClickListener(new Button.OnClickListener(){

					@Override
					public void onClick(View v) {
						Vector<Integer> vOutputPos=posCheck(outputPosEdit.getText().toString());
						/*if(IDEdit.getText().toString().isEmpty())
							Toast.makeText(context, "ID为空",
		       					    Toast.LENGTH_SHORT).show();
						else*/ if(vOutputPos==null)
							Toast.makeText(context, "输入口输入错误",
		       					    Toast.LENGTH_SHORT).show();
						else{
							InComponentBSensor is=new InComponentBSensor(context,posMax(vOutputPos)+1,nextSensorInID++);
							for(Integer outputPos:vOutputPos){
								is.addOutput(outputPos);
							}
							for(int i=0;i<Sensor.INITPARASNUM;i++){
								if(paraEdits[i].getText().toString().equals("")) is.getSensor().addInitParas(0);
								else is.getSensor().addInitParas(Integer.parseInt(paraEdits[i].getText().toString()));
							}
							ComponentsService.setInComSen.add(is);
							finish();	
						}
					}
					
				});
			}
		});
		addOutCom.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				setContentView(R.layout.activity_add_out_com_bind_sensor);
				Button add=(Button)findViewById(R.id.add);
				//final EditText IDEdit=(EditText)findViewById(R.id.IDEdit);
				final EditText inputPosEdit=(EditText)findViewById(R.id.inputPosEdit);
				final EditText[] paraEdits=findParaEdit();
				add.setOnClickListener(new Button.OnClickListener(){

					@Override
					public void onClick(View v) {
						Vector<Integer> vInputPos=posCheck(inputPosEdit.getText().toString());
						/*if(IDEdit.getText().toString().isEmpty())
							Toast.makeText(context, "ID为空",
		       					    Toast.LENGTH_SHORT).show();
						else*/ if(vInputPos==null)
							Toast.makeText(context, "输入口输入错误",
		       					    Toast.LENGTH_SHORT).show();
						else{
							OutComponentBSensor os=new OutComponentBSensor(context,posMax(vInputPos)+1,nextSensorOutID++);
							for(Integer inputPos:vInputPos){
								os.addInput(inputPos,new Input(DataType.N){
									@Override
									protected void unbindProcess() {
									}
								});
							}
							for(int i=0;i<Sensor.INITPARASNUM;i++){
								if(paraEdits[i].getText().toString().equals("")) os.getSensor().addInitParas(0);
								else os.getSensor().addInitParas(Integer.parseInt(paraEdits[i].getText().toString()));
							}
							ComponentsService.setOutComSen.add(os);
							finish();	
						}
					}
					
				});
			}
		});
		addAbstrCom.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				setContentView(R.layout.activity_add_abstr_com);
				Button addNShunt=(Button)findViewById(R.id.addNShunt);
				addNShunt.setOnClickListener(new Button.OnClickListener(){
					@Override
					public void onClick(View v) {
						abstrComAdd(true,true,NSHUNT);
					}
				});
				Button addBShunt=(Button)findViewById(R.id.addBShunt);
				addBShunt.setOnClickListener(new Button.OnClickListener(){
					@Override
					public void onClick(View v) {
						abstrComAdd(true,true,BSHUNT);
					}
				});
				Button addMultiplication=(Button)findViewById(R.id.addMultiplication);
				addMultiplication.setOnClickListener(new Button.OnClickListener(){
					@Override
					public void onClick(View v) {
						abstrComAdd(true,true,MULTIPLICATION);
					}
				});
				Button addGreater=(Button)findViewById(R.id.addGreater);
				addGreater.setOnClickListener(new Button.OnClickListener(){
					@Override
					public void onClick(View v) {
						abstrComAdd(true,true,GREATER);
					}
				});
				Button addLess=(Button)findViewById(R.id.addLess);
				addLess.setOnClickListener(new Button.OnClickListener(){
					@Override
					public void onClick(View v) {
						abstrComAdd(true,true,LESS);
					}
				});
				Button addNConstant=(Button)findViewById(R.id.addNConstant);
				addNConstant.setOnClickListener(new Button.OnClickListener(){
					@Override
					public void onClick(View v) {
						abstrComAdd(true,true,NCONSTANT);
					}
				});
				Button addBConstant=(Button)findViewById(R.id.addBConstant);
				addBConstant.setOnClickListener(new Button.OnClickListener(){
					@Override
					public void onClick(View v) {
						abstrComAdd(true,true,BCONSTANT);
					}
				});
			}
		});
	}
	private EditText[] findParaEdit(){
		EditText[] out=new  EditText[8];
		out[0]=(EditText)findViewById(R.id.para0Edit);
		out[1]=(EditText)findViewById(R.id.para1Edit);
		out[2]=(EditText)findViewById(R.id.para2Edit);
		out[3]=(EditText)findViewById(R.id.para3Edit);
		out[4]=(EditText)findViewById(R.id.para4Edit);
		out[5]=(EditText)findViewById(R.id.para5Edit);
		out[6]=(EditText)findViewById(R.id.para6Edit);
		out[7]=(EditText)findViewById(R.id.para7Edit);
		return out;
	}
	private void abstrComAdd(final boolean shouldSetInputPos,
			final boolean shouldSetOutputPos,
			final int abstrComType){
		if(abstrComType==NCONSTANT){
			setContentView(R.layout.const_com);
			Button add=(Button)findViewById(R.id.add);
			final EditText constEdit=(EditText)findViewById(R.id.constEdit);
			add.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					try{
						Constant co= new Constant(context,Integer.parseInt(constEdit.getText().toString()));
						ComponentsService.setAbstrCom.add(co);
					}catch(NumberFormatException e){
						return;
					}
					finish();
				}
				
			});
			return;
		}
		if(abstrComType==BCONSTANT){
			setContentView(R.layout.b_const_com);
			Button add=(Button)findViewById(R.id.add);
			final CheckBox constCheckBox=(CheckBox)findViewById(R.id.constCheckBox);
			add.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Constant co= new Constant(context,constCheckBox.isActivated());
					ComponentsService.setAbstrCom.add(co);
					finish();
				}
				
			});
			return;
		}
		setContentView(R.layout.abstr_com);
		Button add=(Button)findViewById(R.id.add);
		LinearLayout L=(LinearLayout)findViewById(R.id.L);
		LinearLayout inL=(LinearLayout)findViewById(R.id.inL);
		LinearLayout outL=(LinearLayout)findViewById(R.id.outL);
		final EditText outputPosEdit=(EditText)findViewById(R.id.outputPosEdit);
		final EditText inputPosEdit=(EditText)findViewById(R.id.inputPosEdit);
		if(!shouldSetInputPos) L.removeView(inL);
		if(!shouldSetOutputPos)	L.removeView(outL);
		add.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				AbstrComponent ac=null;
				Vector<Integer> vOutputPos=null;
				Vector<Integer> vInputPos=null;
				if(shouldSetOutputPos){
					vOutputPos=posCheck(outputPosEdit.getText().toString());
					if(vOutputPos==null) return;
				}
				if(shouldSetInputPos){ 
					vInputPos=posCheck(inputPosEdit.getText().toString());
					if(vInputPos==null) return;
				}
				switch(abstrComType){
				case BSHUNT:
					Integer width=null;
					if(vInputPos.elementAt(0)>posMax(vOutputPos)) width=vInputPos.elementAt(0)+1;
					else width=posMax(vOutputPos)+1;
					ac=new Shunt(context,DataType.B,width,vInputPos.elementAt(0),vOutputPos);
					break;
				case NSHUNT:
					if(vInputPos.elementAt(0)>posMax(vOutputPos)) width=vInputPos.elementAt(0)+1;
					else width=posMax(vOutputPos)+1;
					ac=new Shunt(context,DataType.N,width,vInputPos.elementAt(0),vOutputPos);
					break;
				case MULTIPLICATION:
					if(vInputPos.elementAt(1)>vOutputPos.elementAt(0)) width=vInputPos.elementAt(1)+1;
					else width=vOutputPos.elementAt(0)+1;
					ac=new Multiplication(context,width,vInputPos.elementAt(0),vInputPos.elementAt(1),vOutputPos.elementAt(0));
					
					break;
				case GREATER:
					if(vInputPos.elementAt(1)>vOutputPos.elementAt(0)) width=vInputPos.elementAt(1)+1;
					else width=vOutputPos.elementAt(0)+1;
					ac=new Greater(context,width,vInputPos.elementAt(0),vInputPos.elementAt(1),vOutputPos.elementAt(0));
					
					break;
				case LESS:
					if(vInputPos.elementAt(1)>vOutputPos.elementAt(0)) width=vInputPos.elementAt(1)+1;
					else width=vOutputPos.elementAt(0)+1;
					ac=new Less(context,width,vInputPos.elementAt(0),vInputPos.elementAt(1),vOutputPos.elementAt(0));
					break;
				} 
				
				ComponentsService.setAbstrCom.add(ac);
				finish();
				//ComponentsService.setAbstrCom.add(ac);
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_com, menu);
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
	private Vector<Integer> posCheck(String s){
		Vector<Integer> v=new Vector<Integer>();
		String[] posArray = s.split(",");
		try{
			for(String posString:posArray){
				int pos=Integer.parseInt(posString);
				v.add(pos);
			}
		}catch(NumberFormatException e){
			return null;
		}
		return v;
	}
	private Integer posMax(Vector<Integer> v){
		Integer max=null;
		for(Integer i:v){
			if(max==null) max=i;
			else if(max<i) max=i;
		}
		return max;
	}
}
