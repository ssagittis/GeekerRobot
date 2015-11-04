package com.control.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.control.ComponentsService;
import com.control.NetSensorService;
import com.control.ProgramActivity;
import com.control.component.IO.Input;
import com.control.component.IO.Output;
import com.control.component.tools.Point;
import com.control.tools.DataType;
import com.control.tools.Ran;


import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

public class Component extends View implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int UNITWIDTH=70;
	private static final int BIOWIDTH=35;
	private static final int NIOWIDTH=45;
	private static final int VIOWIDTH=25;
	private static boolean deleteCondition=false;
	public static boolean isDeleteCondition() {
		return deleteCondition;
	}
	public static void setDeleteCondition(boolean deleteCondition) {
		Component.deleteCondition = deleteCondition;
	}
	private Integer getIOWidth(int dataType){
		switch(dataType){
		case DataType.B:
			return BIOWIDTH;
		case DataType.N:
			return NIOWIDTH;
		case DataType.V:
			return VIOWIDTH;
		default:
			return null;
		}
	}
	private String getDataTypeName(int dataType){
		switch(dataType){
		case DataType.B:
			return "B";
		case DataType.N:
			return "N";
		case DataType.V:
			return "V";
		default:
			return null;
		}
	}
	private static final int TEXTSIZE=BIOWIDTH/2;
	protected SparseArray<Output> sArrayOutputPos=null;
	protected SparseArray<Input> sArrayInputPos=null;
	private String name="";
	private Point pos;//左上角坐标
	private int width;//宽是一个单位的几倍,高就是一个单位
	private int color=Color.rgb(200-Ran.getInt(120),200-Ran.getInt(120),200-Ran.getInt(120));
	private int colorSetModel=Color.parseColor("#80000000");
	protected static Set<Component> setAll=new HashSet<Component>();
	
	Paint  paint;
	Paint  paintText;
	private Context context;
	public Component(Context context,Point pos,int width) {
		this(context,pos,width,"");
	}
	public Component(Context context,Point pos,int width,String name) {
		super(context);
		this.pos=pos;this.width=width;this.name=name;this.context=context;
		paint = new Paint();
	    paint.setAntiAlias(true);
	    paint.setStyle(Style.FILL);//FILL
	  	paint.setColor(color);
	    paint.setAlpha(200);
	    paintText  = new Paint();
	    paintText.setAntiAlias(true);
	    paintText.setColor(Color.parseColor("#000000"));
	    paintText.setTextSize(TEXTSIZE);
	    setAll.add(this);
	}
	public void delete(){
		if(sArrayOutputPos!=null){
			for(int i = 0; i < sArrayOutputPos.size(); i++){
				 int outputPos = sArrayOutputPos.keyAt(i);
				 sArrayOutputPos.get(outputPos).unbindInput();
			}
		}
		if(sArrayInputPos!=null){
			for(int i = 0; i < sArrayInputPos.size(); i++){
				 int inputPos = sArrayInputPos.keyAt(i);
				 sArrayInputPos.get(inputPos).unbindOutput();
			}
		}
		setAll.remove(this);
	}
	protected boolean addInput(int pos,Input i){
		if(sArrayInputPos==null) sArrayInputPos=new SparseArray<Input>();
		if(pos>=width) return false;
		sArrayInputPos.append(pos, i);
		return true;
	}
	protected boolean addOutput(int pos,Output o){
		if(sArrayOutputPos==null) sArrayOutputPos=new SparseArray<Output>();
		if(pos>=width) return false;
		sArrayOutputPos.append(pos, o);
		return true;
	}
	/*public Component(Context context,AttributeSet attrs) {
		super(context,attrs);
		mPaint = new Paint();
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Component);
		int textColor = array.getColor(R.styleable.Component_textColor, 0XEE33AA00);
		float textSize = array.getDimension(R.styleable.Component_textSize, 36);
		
		//获取中心点
		Center = new Point(array.getInteger(R.styleable.Component_positionX, 150),array.getInteger(R.styleable.Component_positionY, 150));
		
		//初始化六个定点
		Vectex = new Point[6];
		updateVectex();
		
		array.recycle();
		
		paint = new Paint();
	    paint.setAntiAlias(true);
	    paint.setStyle(Style.FILL);//FILL
	  	paint.setColor(Color.parseColor("#F488A3"));
	    paint.setAlpha(200);
	}*/
	
	//通过中心点和边长来求出六个定点的坐标
	/*private void updateVectex(){
		Vectex[0] = new Point(Center.x+sideLength, Center.y);
		Vectex[1] = new Point(Center.x+sideLength/2, (float) (Center.y+sideLength*Math.sin(Math.PI/3)));
		Vectex[2] = new Point(Center.x-sideLength/2, (float) (Center.y+sideLength*Math.sin(Math.PI/3)));
		Vectex[3] = new Point(Center.x-sideLength, Center.y);
		Vectex[4] = new Point(Center.x-sideLength/2, (float) (Center.y-sideLength*Math.sin(Math.PI/3)));
		Vectex[5] = new Point(Center.x+sideLength/2, (float) (Center.y-sideLength*Math.sin(Math.PI/3)));
	}*/

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		Path path = new Path();
		path.moveTo(pos.x, pos.y);
		if(sArrayInputPos!=null){
			for(int i = 0; i < sArrayInputPos.size(); i++){
				 int inputPos = sArrayInputPos.keyAt(i);
				 int iowidth=getIOWidth(sArrayInputPos.get(inputPos).getDataType());
				 
				 path.lineTo(pos.x+inputPos*UNITWIDTH+UNITWIDTH/2-iowidth/2, pos.y);
				 path.lineTo(pos.x+inputPos*UNITWIDTH+UNITWIDTH/2-iowidth/2, pos.y+iowidth);
				 path.lineTo(pos.x+inputPos*UNITWIDTH+UNITWIDTH/2+iowidth/2, pos.y+iowidth);
				 path.lineTo(pos.x+inputPos*UNITWIDTH+UNITWIDTH/2+iowidth/2, pos.y);
			}
		}
		path.lineTo(pos.x+width*UNITWIDTH, pos.y);
		path.lineTo(pos.x+width*UNITWIDTH, pos.y+UNITWIDTH);
		if(sArrayOutputPos!=null){
			for(int i = 0; i < sArrayOutputPos.size(); i++){
				 int outputPos = sArrayOutputPos.keyAt(i);
				 int iowidth=getIOWidth(sArrayOutputPos.get(outputPos).getDataType());

				 path.lineTo(pos.x+outputPos*UNITWIDTH+UNITWIDTH/2-iowidth/2, pos.y+UNITWIDTH);
				 path.lineTo(pos.x+outputPos*UNITWIDTH+UNITWIDTH/2-iowidth/2, pos.y+UNITWIDTH+iowidth);
				 path.lineTo(pos.x+outputPos*UNITWIDTH+UNITWIDTH/2+iowidth/2, pos.y+UNITWIDTH+iowidth);
				 path.lineTo(pos.x+outputPos*UNITWIDTH+UNITWIDTH/2+iowidth/2, pos.y+UNITWIDTH);
			}
		}
		path.lineTo(pos.x, pos.y+UNITWIDTH);
		path.lineTo(pos.x, pos.y);
		path.close();
		canvas.drawPath(path, paint);
		if(sArrayInputPos!=null){
			for(int i = 0; i < sArrayInputPos.size(); i++){
				 int inputPos = sArrayInputPos.keyAt(i);
				 int iowidth=this.getIOWidth(sArrayInputPos.get(inputPos).getDataType());
				 if(sArrayInputPos.get(inputPos).isBind()) paintText.setColor(Color.RED);
				 canvas.drawText(getDataTypeName(sArrayInputPos.get(inputPos).getDataType()),
						 pos.x+inputPos*UNITWIDTH+UNITWIDTH/2-TEXTSIZE/3,
						 pos.y+iowidth/2+TEXTSIZE/3, paintText);  
				 paintText.setColor(Color.BLACK);
			}
		}
		if(sArrayOutputPos!=null){
			for(int i = 0; i < sArrayOutputPos.size(); i++){
				 int outputPos = sArrayOutputPos.keyAt(i);
				 int iowidth=this.getIOWidth(sArrayOutputPos.get(outputPos).getDataType());
				 if(sArrayOutputPos.get(outputPos).isBind()) paintText.setColor(Color.RED);
				 canvas.drawText(getDataTypeName(sArrayOutputPos.get(outputPos).getDataType()),
						 pos.x+outputPos*UNITWIDTH+UNITWIDTH/2-TEXTSIZE/3,
						 pos.y+iowidth/2+UNITWIDTH+TEXTSIZE/3, paintText);
				 paintText.setColor(Color.BLACK);
			}
		}
		canvas.drawText(name, pos.x,pos.y+UNITWIDTH, paintText);
	}
	Timer timer;
	Point downPoint = new Point(0,0);//点击时的坐标
	
	//isDown在MotionEvent.ACTION_DOWN置为真，
	//在MotionEvent.ACTION_UP置为假，
	//在MotionEvent.ACTION_MOVE依据isDown是否为真判断图形是否该移动。
	//MotionEvent.ACTION_MOVE中，若isDown为假，向外传播触摸消息，否则不向外传播
	boolean isDown = false;
	//isSetModel表示是否在设定模式，只有在设定模式才能移动和更改属性
	//isSetModel为真的生命比isDown短，且一直包裹在isDown为真的生命内
	//按下一段时间后，且移动距离在一定阈值内，isSetModel才会为真
	boolean isSetModel = false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!deleteCondition){
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				float x = event.getX();
				float y = event.getY();
				//isTouch = isIn(x, y);
				if(isIn(x, y)){
					isDown = true;
					downPoint.x = x;
					downPoint.y = y;
					paint.setColor(colorSetModel);
					invalidate();
					timer = new Timer();
					timer.schedule(new TimerTask() {
						
						@Override
						public void run() {
							isSetModel = true;//按了一段时间后就进入设定模式
							if(sArrayInputPos!=null){
								for(int i = 0; i < sArrayInputPos.size(); i++){
									int inputPos = sArrayInputPos.keyAt(i);
									sArrayInputPos.get(inputPos).unbindOutput();
								}
							}
							if(sArrayOutputPos!=null){
								for(int i = 0; i < sArrayOutputPos.size(); i++){
									int outputPos = sArrayOutputPos.keyAt(i);
									sArrayOutputPos.get(outputPos).unbindInput();
								}
							}
						}
					}, 50);
				}
				//按其他地方取消设定模式
				else{
					isSetModel = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				isDown = false;
				isSetModel = false;
				paint.setColor(color);
				Component nearest=null;
				Input inNearest=null;
				Output outNearest=null;
				int xDiffFromNearest=0;//如果有较近的组件，匹配放置后当前组件横坐标与匹配组件横坐标差值（单位长度的倍数）
				boolean isPutBelow=true;//如果有较近的组件，匹配放置后当前组件放在其上面还是下面
				float distance2 =Float.MAX_VALUE;//存最小距离
				if(sArrayInputPos!=null){
					for(int i = 0; i < sArrayInputPos.size(); i++){//遍历当前组件所有输入口
						int inputPos = sArrayInputPos.keyAt(i);//得到当前组某个有输入口的位置下标
						for(Component v:setAll){//遍历现场所有组件
							if(v!=this){
								if(v.sArrayOutputPos!=null){
									for(int j=0;j<v.sArrayOutputPos.size();j++){//遍历现场某个其他组件的所有输出口
										int outputPosOfV =v.sArrayOutputPos.keyAt(j);//当前组件某个输出口的位置下标
										if(notblockedByOtherIO(inputPos,sArrayInputPos,width,outputPosOfV,v.sArrayOutputPos,v.width)){//如果匹配被遮挡，就放弃计算距离
											if(v.sArrayOutputPos.get(outputPosOfV).getDataType()==sArrayInputPos.get(inputPos).getDataType()){//如果IO口类型不一致，就放弃计算距离
												float distance2Curr=distance2(pos.x+inputPos*UNITWIDTH,pos.y,
														v.pos.x+outputPosOfV*UNITWIDTH,v.pos.y+UNITWIDTH);
												if(distance2>distance2Curr){
													distance2=distance2Curr;
													xDiffFromNearest=outputPosOfV-inputPos;
													isPutBelow=true;
													nearest=v;
													outNearest=v.sArrayOutputPos.get(outputPosOfV);
													inNearest=sArrayInputPos.get(inputPos);
												}
											}
										}
									}
								}
							}
						}
					}
				}
				if(sArrayOutputPos!=null){
					for(int i = 0; i < sArrayOutputPos.size(); i++){//遍历当前组件所有口
						int outputPos = sArrayOutputPos.keyAt(i);//得到当前组某个有输出口的位置下标
						for(Component v:setAll){//遍历现场所有组件
							if(v!=this){
								if(v.sArrayInputPos!=null){
									for(int j=0;j<v.sArrayInputPos.size();j++){//遍历现场某个其他组件的所有输入口
										int inputPosOfV =v.sArrayInputPos.keyAt(j);//当前组件某个输入口的位置下标
											
										if(notblockedByOtherIO(outputPos,sArrayOutputPos,width,inputPosOfV,v.sArrayInputPos,v.width)){//如果匹配被遮挡，就放弃计算距离
											if(v.sArrayInputPos.get(inputPosOfV).getDataType()==sArrayOutputPos.get(outputPos).getDataType()){//如果IO口类型不一致，就放弃计算距离
												float distance2Curr=distance2(pos.x+outputPos*UNITWIDTH,pos.y+UNITWIDTH,
														v.pos.x+inputPosOfV*UNITWIDTH,v.pos.y);
												if(distance2>distance2Curr){
													distance2=distance2Curr;
													xDiffFromNearest=inputPosOfV-outputPos;
													isPutBelow=false;
													nearest=v;
													outNearest=sArrayOutputPos.get(outputPos);
													inNearest=v.sArrayInputPos.get(inputPosOfV);
												}
											}
										}
									}
								}
							}
						}
					}
				}
				if(distance2<2000.0&&outNearest!=null&&inNearest!=null){
					pos.x=nearest.pos.x+xDiffFromNearest*UNITWIDTH;
					pos.y=nearest.pos.y+(isPutBelow?UNITWIDTH:(-UNITWIDTH));
					inNearest.bindOutput(outNearest);
					Intent intent = new Intent();  
		            intent.setAction(ComponentsService.actionComRenew);
					context.sendBroadcast(intent);
				}
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				if(isDown){
					if(isSetModel){
						pos.x = event.getX()-UNITWIDTH*width/2;
						pos.y = event.getY()-UNITWIDTH/2;
						invalidate();
					}
					//以下判断移动距离是否超过阈值并且按下时间不超过阈值，同时满足则按上面这段代码不动图形
					double dx = Math.sqrt((event.getX()-downPoint.x)*(event.getX()-downPoint.x)+(event.getY()-downPoint.y)*(event.getY()-downPoint.y));
					if(dx>20 && timer != null){
						timer.cancel();
						timer = null;
					}
				}
				break;
			}
			if(isDown) return true;
			else return false;
		}else{
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				delete();
				if(this instanceof InComponentBSensor) ComponentsService.setInComSen.remove((InComponentBSensor)this);
				if(this instanceof OutComponentBSensor)ComponentsService.setOutComSen.remove((OutComponentBSensor)this);
				if(this instanceof AbstrComponent)ComponentsService.setAbstrCom.remove((AbstrComponent)this);
				ProgramActivity.deletedCom=this;
				Intent intent = new Intent();
				//Bundle bundle = new Bundle();
				//bundle.putSerializable("comDeleted", this);
				//intent.putExtras(bundle);
				intent.setAction(ProgramActivity.actionDeleteCertainCom);
				context.sendBroadcast(intent);
				return false;
			}
			return false;
		}
	}
	private boolean notblockedByOtherIO(int x1,SparseArray<?> a1,int l1,int x2,SparseArray<?> a2,int l2){//两个组件的IO口要匹配时是否被其他IO口挡住，x为下标，a为稀疏数组，l为长度
		if(l1-l2<x1-x2){
			if(x1>x2){
				for(int i=0;i<(l1-x1+x2);i++){
					if(((a1.indexOfKey(x1-x2+i)>=0)&&(a2.indexOfKey(i)<0))||
							((a1.indexOfKey(x1-x2+i)<0)&&(a2.indexOfKey(i)>=0))) return false;
				}
			}
			else{
				for(int i=0;i<l1;i++){
					if(((a1.indexOfKey(i)>=0)&&(a2.indexOfKey(x2-x1+i)<0))||
							((a1.indexOfKey(i)<0)&&(a2.indexOfKey(x2-x1+i)>=0))) return false;
				}
			}
		}
		else{
			if(x1>x2){
				for(int i=0;i<l2;i++){
					if(((a1.indexOfKey(x1-x2+i)>=0)&&(a2.indexOfKey(i)<0))||
							((a1.indexOfKey(x1-x2+i)<0)&&(a2.indexOfKey(i)>=0))) return false;
				}
			}
			else{
				for(int i=0;i<(l2-x2+x1);i++){
					if(((a1.indexOfKey(i)>=0)&&(a2.indexOfKey(x2-x1+i)<0))||
							((a1.indexOfKey(i)<0)&&(a2.indexOfKey(x2-x1+i)>=0))) return false;
				}
			}
		}
		return true;
	}
	private float distance2(float x1,float y1,float x2,float y2){
		return (x2-x1)*(x2-x1)+(y2-y1)*(y2-y1);
	}
	//判断某个点是否在多边形内
	private boolean isIn(float x,float y){
		//boolean isLeft = false;
		boolean isIn = true;
		if(x<pos.x) isIn=false;
		if(x>pos.x+UNITWIDTH*width) isIn=false;
		if(y<pos.y) isIn=false;
		if(y>pos.y+UNITWIDTH) isIn=false;
		return isIn;
	}
}
