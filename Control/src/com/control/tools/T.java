package com.control.tools;

import android.content.Context;
import android.widget.Toast;

public class T {
	public static void toast(Context c,String s){
		Toast.makeText(c,s,
			    Toast.LENGTH_SHORT).show();
	} 
}
