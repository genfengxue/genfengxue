/**
 * 注册/登陆界面@author vita
 */

package com.genfengxue.windenglish.activities;

import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.network.JsonApiCaller;
import com.genfengxue.windenglish.utils.UriUtils;

public class LoginActivity extends Activity {
	
	private Button tLoginButton;
	private EditText tUserNo;
	private EditText tPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		tLoginButton = (Button)  findViewById(R.id.loginButton);
		tUserNo 	 = (EditText)findViewById(R.id.loginText);
		tPassword    = (EditText)findViewById(R.id.loginText2);
		
		initListener();
	}
	
	private void initListener() {
		tLoginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int    userNo   = Integer.parseInt(tUserNo.getText().toString());
				String password = tPassword.getText().toString();
				if (userNo <= 0 || password.length() == 0) {
					Toast.makeText(LoginActivity.this, "用户编码和密码都不得为空", Toast.LENGTH_SHORT).show();
					return;
				}
				Log.i("wind", "I click loginButton");
				new LoginTask(userNo, password).execute();
			}
		});  
		
		//监听输入框
		TextWatcher textWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				int userNoLength   = tUserNo.getText().toString().length();  
				int passwordLength = tPassword.getText().toString().length();  
				if (userNoLength > 0 && passwordLength > 0) {
					tLoginButton.setBackgroundColor(Color.parseColor("#691281"));
				} else {
					tLoginButton.setBackgroundColor(Color.parseColor("#aaaaaa"));
				}
			}
		};
		
		tUserNo.addTextChangedListener(textWatcher);
		tPassword.addTextChangedListener(textWatcher);
	}

	private class LoginTask extends AsyncTask<Void, Void, String> {
		private int userNo;
		private String password;
		private ProgressDialog progressDialog;
		
		public LoginTask(int userNo, String password) {
			this.userNo = userNo;
			this.password = password;
			this.progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.setTitle("正在登录……");
		}
		
		protected void onPreExecute() {
			progressDialog.show();
		}
		
		@Override
		protected String doInBackground(Void... params) {
			//获取token
			String tokenObjString = JsonApiCaller.postTokenApi(userNo, password);
			if (tokenObjString == null) return null;
			
			String token = "";
			try {
				JSONObject tokenObj = new JSONObject(tokenObjString);
				token = tokenObj.getString("token");
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
			
			//根据token获取user profile，并存入本地文件
			String userProfileString = JsonApiCaller.getUserProfileApi(token);
			if (userProfileString == null) return null;
			
			try {
				JSONObject userProfileJson = new JSONObject(userProfileString);
				userProfileJson.put("accessToken", token);
				userProfileJson.put("password", password);
				
				FileWriter userFile = new FileWriter(UriUtils.getUserDataPath());
				userFile.write(userProfileJson.toString());
				userFile.close();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return token;
		}
		
		protected void onPostExecute(String token) {
			progressDialog.dismiss();
			if (token == null) {
				Toast.makeText(LoginActivity.this, "登录失败，请重试……", Toast.LENGTH_SHORT).show();
				return;
			}
			//登录成功之后，进入LearnActivity
			Intent intent = new Intent(LoginActivity.this, LearnActivity.class);
			LoginActivity.this.startActivity(intent);
			LoginActivity.this.finish();
		}
	}
}
