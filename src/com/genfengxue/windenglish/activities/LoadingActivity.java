package com.genfengxue.windenglish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.mgr.AccountMgr;
import com.genfengxue.windenglish.struct.UserProfile;
import com.genfengxue.windenglish.utils.FunctionUtils;

public class LoadingActivity extends Activity {

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading);
		FunctionUtils.setupApp();

		new CheckUserTask().execute();
	}
	
	private void goLogin() {
		Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
		goActivity(intent);
	}

	private void goUpdateProfile() {
		Intent intent = new Intent(LoadingActivity.this, ProfileActivity.class);
		goActivity(intent);
	}
	
	private void goLearn() {
		Intent intent = new Intent(LoadingActivity.this, LearnActivity.class);
		goActivity(intent);
	}
	
	private void goActivity(final Intent intent) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(intent);
				LoadingActivity.this.finish();
			}
		}, 1000);
	}
	
	private class CheckUserTask extends AsyncTask<Void, Void, UserProfile> {

		@Override
		protected UserProfile doInBackground(Void... params) {
			return AccountMgr.getUserProfile(LoadingActivity.this);
		}
		
		protected void onPostExecute(UserProfile user) {
			if (user == null) {
				goLogin();
			} else {
				if (user.isNeedUpdate()) {
					goUpdateProfile();
				} else {
					goLearn();
				}
			}
	    }
	}
}
