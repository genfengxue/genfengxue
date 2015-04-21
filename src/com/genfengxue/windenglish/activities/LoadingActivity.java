package com.genfengxue.windenglish.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.mgr.AccountMgr;
import com.genfengxue.windenglish.struct.UserProfile;
import com.genfengxue.windenglish.utils.Constants;
import com.genfengxue.windenglish.utils.FunctionUtils;

public class LoadingActivity extends Activity {

	private static final String TAG = "LoadingActivity";
	
	private TextView versionName;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading);
		
		versionName = (TextView)findViewById(R.id.version_name);
	}
	
	protected void onStart() {
		super.onStart();
		PackageInfo pi;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName.setText(pi.versionName);
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		
		if (!checkExternalStorage()) {
			goActivity(null);
			return;
		}
		
		FunctionUtils.setupApp();

		new CheckUserTask().execute();		
	}
	
	private boolean checkExternalStorage() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return true;
		} else {
			Toast.makeText(this, R.string.external_storage_unmounted, Toast.LENGTH_SHORT).show();
			return false;
		}
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
				if (intent != null)
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
