/**
 * 注册/登陆界面@author vita
 */

package com.genfengxue.windenglish.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
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
			//这里应该是登录的过程
			String tokenObjString = JsonApiCaller.postTokenApi(userNo, password);
			String token = "";
			try {
				JSONObject tokenObj = new JSONObject(tokenObjString);
				token = tokenObj.getString("token");
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
			return token;
		}
		
		protected void onPostExecute(String token) {
			progressDialog.dismiss();
			Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();;
//			Intent intent = new Intent(LoginActivity.this, LearnActivity.class);
//			LoginActivity.this.startActivity(intent);
//			LoginActivity.this.finish();
		}
	}
}
