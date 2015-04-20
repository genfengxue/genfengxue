package com.genfengxue.windenglish.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.mgr.AccountMgr;
import com.genfengxue.windenglish.struct.UserProfile;
import com.genfengxue.windenglish.utils.Constants;
import com.genfengxue.windenglish.utils.FunctionUtils;

public class LoadingActivity extends Activity {

	private TextView versionName;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading);
		
		versionName = (TextView)findViewById(R.id.version_name);
		PackageInfo pi;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName.setText("v" + pi.versionName);//版本号前面加个v作为前缀吧^_^
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			versionName.setText("");
		}
		
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
		SharedPreferences pref = getSharedPreferences(Constants.COURSE_STATE_PREF, MODE_PRIVATE);
		if (pref.contains("courseNo")) {
			Intent intent = new Intent(LoadingActivity.this, LearnActivity.class);
			intent.putExtra("courseNo", pref.getInt("courseNo", 2));
			goActivity(intent);
		} else {
			Intent intent = new Intent(LoadingActivity.this, CourseActivity.class);
			goActivity(intent);
		}
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
