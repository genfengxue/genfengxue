package com.genfengxue.windenglish.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.mgr.AccountMgr;
import com.genfengxue.windenglish.network.JsonApiCaller;
import com.genfengxue.windenglish.utils.Constants;

public class ProfileActivity extends Activity {

	private static String TAG = "ProfileActivity";
	private static final int ERROR_EMAIL_DUPLICATE = 10020;
	private static final int ERROR_NICKNAME_DUPLICATE = 10030;
	
	private String token, email, nickname;
	private SharedPreferences pref;
	
	private EditText emailEt, nicknameEt;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
//		getActionBar().setTitle(R.string.submit_profile);

		pref = getSharedPreferences(
				Constants.USER_PROFILE_PREF, MODE_PRIVATE);
		token = pref.getString("token", "");
		if (token.equals("")) {
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			finish();
		}
		email = pref.getString("email", "");
		nickname = pref.getString("nickname", "");
		
		emailEt = (EditText) findViewById(R.id.email_txt);
		nicknameEt = (EditText) findViewById(R.id.nickname_txt);
		
		emailEt.setText(email);
		nicknameEt.setText(nickname);
		
		((Button) findViewById(R.id.submit_profile)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				submit();
			}
		});
	}
	
	private void submit() {
		String newEmail = emailEt.getText().toString().trim();
		String newNickname = nicknameEt.getText().toString().trim();
		if (newEmail.equals(email)) {
			newEmail = null;
		}
		if (newNickname.equals(nickname)) {
			newNickname = null;
		}
		if (newEmail != null || newNickname != null) {
			new SubmitTask().execute(token, newEmail, newNickname);
		} else {
			Toast.makeText(this, R.string.same_hint, 
					Toast.LENGTH_SHORT).show();
		}
	}
	
	private class SubmitTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String res = JsonApiCaller.postUpdateProfile(params[0], params[1], params[2]);
			res = res != null ? res.trim() : res;
			if ("OK".equals(res)) {
				AccountMgr.getUserProfile(ProfileActivity.this, true);
			}
			return res;
		}
		
		protected void onPostExecute(String result) {
			// TODO hardcoded should be replaced by new api requester
			if ("OK".equals(result)) {
				Toast.makeText(ProfileActivity.this, R.string.update_profile_success, 
						Toast.LENGTH_SHORT).show();
				Intent learn = new Intent(ProfileActivity.this, CourseActivity.class);
				startActivity(learn);
				finish();
			} else {
				try {
					JSONObject obj = new JSONObject(result);
					int code = obj.optInt("errCode", 0);
					switch (code) {
					case ERROR_EMAIL_DUPLICATE:
						Toast.makeText(ProfileActivity.this, 
								R.string.err_email_dup, Toast.LENGTH_SHORT).show();
						break;
					case ERROR_NICKNAME_DUPLICATE:
						Toast.makeText(ProfileActivity.this, 
								R.string.err_nickname_dup, Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (JSONException e) {
					Log.e(TAG, "update profile failed: " + e.getMessage());
				}
			}
		}
	}
}
