/**
 * 注册/登陆界面@author vita
 */

package com.genfengxue.windenglish.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.mgr.AccountMgr;

public class LoginActivity extends Activity {
	
	private Button loginButton;
	private EditText userNoText;
	private EditText passwordText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		loginButton = (Button)  findViewById(R.id.loginButton);
		userNoText 	 = (EditText)findViewById(R.id.loginText);
		passwordText    = (EditText)findViewById(R.id.loginText2);
		
		initListener();
	}
	
	private void initListener() {
		loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int    userNo   = Integer.parseInt(userNoText.getText().toString());
				String password = passwordText.getText().toString();
				if (userNo <= 0 || password.length() == 0) {
					Toast.makeText(LoginActivity.this, R.string.login_tip, Toast.LENGTH_SHORT).show();
					return;
				}
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
				int userNoLength   = userNoText.getText().toString().length();  
				int passwordLength = passwordText.getText().toString().length();  
				if (userNoLength > 0 && passwordLength > 0) {
					loginButton.setBackgroundColor(Color.parseColor("#691281"));
				} else {
					loginButton.setBackgroundColor(Color.parseColor("#aaaaaa"));
				}
			}
		};
		
		userNoText.addTextChangedListener(textWatcher);
		passwordText.addTextChangedListener(textWatcher);
	}

	private class LoginTask extends AsyncTask<Void, Void, Boolean> {
		private int userNo;
		private String password;
		private ProgressDialog progressDialog;
		
		public LoginTask(int userNo, String password) {
			this.userNo = userNo;
			this.password = password;
			this.progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.setTitle(R.string.on_login);
		}
		
		protected void onPreExecute() {
			progressDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			if (AccountMgr.updateToken(userNo, password) 
					&& AccountMgr.getUserProfile() != null) {
				return true;
			}
			
			return false;
		}
		
		protected void onPostExecute(Boolean success) {
			progressDialog.dismiss();
			if (!success) {
				Toast.makeText(LoginActivity.this, R.string.login_failed, 
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			Intent intent = new Intent(LoginActivity.this, LearnActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
