/**
 * 注册/登陆界面@author vita
 */

package com.genfengxue.windenglish.activities;

import java.io.File;
import java.io.FileOutputStream;

import com.genfengxue.windenglish.Main;
import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.R.id;
import com.genfengxue.windenglish.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener; 
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity{
	
	private Button loginButton;
	private int flag=1;
	private EditText inputUsername;
	private EditText inputEmail;
	private Context  context = this;  
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//从输入框获得username
			String username="";
			String email="";
			inputUsername =(EditText)findViewById(R.id.loginText);  
			inputEmail = (EditText)findViewById(R.id.loginText2);  
	        username=inputUsername.getText().toString();  
	        email=inputEmail.getText().toString();  
	        if(username.length()==0)
	        {
				flag=0;
	        	Dialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
	        	.setMessage("用户名不能为空！")
	        	.setCancelable(false)
	        	.setPositiveButton("确定",null)
	        	.create();
	        	alertDialog.show();
	        }
	        else if(email.length()==0)
	        {
				flag=0;
	        	Dialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
	        	.setMessage("邮箱不能为空！")
	        	.setCancelable(false)
	        	.setPositiveButton("确定",null)
	        	.create();
	        	alertDialog.show();
	        }
	        else if(username.indexOf("?")>=0)
	        {
	        	flag=0;
	        	Dialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
	        	.setMessage("用户名中不能出现特殊字符“？”")
	        	.setCancelable(false)
	        	.setPositiveButton("确定",null)
	        	.create();
	        	alertDialog.show();
	        }
	        else 
	        	flag=1;
	        
	       
	        
	        if(flag!=0){
	        //创建SD卡录音文件路径
	        String route=Environment.getExternalStorageDirectory().getAbsolutePath();
	        File file = new File(route + "/windenglish");
	        file.mkdirs();
	        //将获得的username存储到文件，并初始化用户信息
			String fileName="userdata";
			String suprise=username;
	        username=username+"?";
	        for(int i=1;i<=1000;i++)
	        {
	        	username=username+"0";
	        }
	        FileOutputStream out = null;  
	        try {  
	            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);  
	            out.write(username.getBytes("UTF-8"));  
		        out.close();
	        } catch (Exception e) {  
				// TODO Auto-generated catch block
	            e.printStackTrace();  
	        }  
	        
	        fileName = "email";
	        try {  
	            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);  
	            out.write(email.getBytes("UTF-8"));  
		        out.close();
	        } catch (Exception e) {  
				// TODO Auto-generated catch block
	            e.printStackTrace();  
	        }  
	        //跳转到主界面

	        //j4f
	        if(suprise.equals("Joure19940531"))
	        {
	            Dialog alertDialogOnPlay = new AlertDialog.Builder(LoginActivity.this)
	            							.setMessage("你喜欢我嘛？")
	            							.setCancelable(false)
	            							.setPositiveButton("喜欢！",new DialogInterface.OnClickListener() {
	            								
	            								@Override
	            								public void onClick(DialogInterface dialog, int which) {
	            									// TODO Auto-generated method stub
	            									Intent intent = new Intent(LoginActivity.this, Main.class);
	            									startActivity(intent);
	            									LoginActivity.this.finish();
	            								}
	            							})
	            							.setNegativeButton("不喜欢",new DialogInterface.OnClickListener() {
	            								@Override
	            								public void onClick(DialogInterface dialog, int which) {
	            									// TODO Auto-generated method stub
	            									;
	            									Dialog alertDialogExitDialog = new AlertDialog.Builder(LoginActivity.this)
	            									.setMessage("哼，再见！")
	            									.setCancelable(false)
	    	            							.setPositiveButton("强制退出",new DialogInterface.OnClickListener() {
	    	            								
	    	            								@Override
	    	            								public void onClick(DialogInterface dialog, int which) {
	    	            									// TODO Auto-generated method stub
	    	            									LoginActivity.this.finish();
	    	            								}
	    	            							})
	            									.create();
	            									alertDialogExitDialog.show();
	            								}
	            							})
	            							.create();
	            alertDialogOnPlay.show();
	        }
	        else {
				Intent intent = new Intent(LoginActivity.this, LearnActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
			}
		}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		loginButton = (Button)this.findViewById(R.id.loginButton);
		inputUsername = (EditText)this.findViewById(R.id.loginText);
		inputEmail = (EditText)this.findViewById(R.id.loginText2);
        loginButton.setOnClickListener(listener);  
        
        //监听输入框
        TextWatcher textWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
		        int length= inputUsername.getText().toString().length();  
		        int length2 = inputEmail.getText().toString().length();  
		        if(length>0 && length2>0)
		        {
		        	loginButton.setBackgroundColor(Color.parseColor("#691281"));
		        }
		        else {
		        	loginButton.setBackgroundColor(Color.parseColor("#aaaaaa"));
				}
			}
		};
        
        inputUsername.addTextChangedListener(textWatcher);
        inputEmail.addTextChangedListener(textWatcher);
		
	}
	
	
}
