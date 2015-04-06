/**
 * 程序初始化@author vita
 */

package com.genfengxue.windenglish;

import java.io.File;

import com.genfengxue.windenglish.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class Loading extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading);
		//在loading界面停留2s
		/*
		 * try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		 File file2 = new File("/data/data/com.example.windenglish/files/tempSC");
	        if(file2.exists())
	        {
	        	file2.delete();
	        }
		
		final Intent intent = new Intent(Loading.this, Login.class);
		final Intent intentMain = new Intent(Loading.this, Main.class);
		File f=new File("/data/data/com.example.windenglish/files/userdata");
        //如果userdata不存在，则跳转到login界面
		if(!f.exists())
        {  
			new Handler().postDelayed(new Runnable() {      
				@Override  
			    public void run() {  
		         	startActivity(intent);
		        	Loading.this.finish();
				}  
			}, 2000);  
        }   
        else 
        {
        	new Handler().postDelayed(new Runnable() {      
				@Override  
			    public void run() {  
		         	startActivity(intentMain);
		        	Loading.this.finish();
				}  
			}, 2000);  
        }
		


	}
}
