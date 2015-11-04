package com.control.tools.onlyCheckBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.control.ControlActivity.WidgetL;

import android.content.Context;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OnlyCheckBoxHelper{
	private List<CheckBox> l=new ArrayList<CheckBox>();
	public OnlyCheckBoxHelper(){
	}
	public void add(CheckBox cb){
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				for (CheckBox c:l){
					c.setChecked(false);
				}
				buttonView.setChecked(isChecked);
			}		
		});
		l.add(cb);
	}
	public String textOfWhichIsChecked(){
		for(int i=0;i<l.size();i++){
			if(l.get(i).isChecked()) return l.get(i).getText()+"";
		}
		return null;
	}
	public int indexOfWhichIsChecked(){
		for(int i=0;i<l.size();i++){
			if(l.get(i).isChecked()) return i;
		}
		return -1;
	}
	public int size(){
		return l.size();
	}
}
