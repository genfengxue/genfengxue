package com.genfengxue.windenglish.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.mgr.AccountMgr;
import com.genfengxue.windenglish.network.JsonApiCaller;
import com.genfengxue.windenglish.network.JsonStringHandler;
import com.genfengxue.windenglish.struct.CourseInfo;
import com.genfengxue.windenglish.struct.UserProfile;
import com.genfengxue.windenglish.ui.CourseAdapter;
import com.genfengxue.windenglish.utils.Constants;

public class CourseActivity extends Activity {

	private static final String TAG = "CourseActivity";
	
	private ListView courseViem;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		courseViem = (ListView) findViewById(R.id.videoList);
		courseViem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CourseAdapter adapter = (CourseAdapter) parent.getAdapter();
				CourseInfo info = adapter.getItem(position);
				Intent intent = new Intent(CourseActivity.this, LearnActivity.class);
				int courseNo = info.getCourseNo();
				intent.putExtra("courseNo", courseNo);
				Editor editor = getSharedPreferences(Constants.COURSE_STATE_PREF, MODE_PRIVATE).edit();
				editor.putInt("courseNo", courseNo);
				editor.apply();
				startActivity(intent);
			}
		});
		
		((ImageView) findViewById(R.id.refreshBtn)).setVisibility(View.INVISIBLE);
		((TextView) findViewById(R.id.mainUsername)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CourseActivity.this, ProfileActivity.class);
				startActivity(intent);
			}
		});
		
		((ImageView) findViewById(R.id.go_webview)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CourseActivity.this, WebviewActivity.class);
				startActivity(intent);
			}
		});

		new UpdateCourseTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		UserProfile profile = AccountMgr.getUserProfile(this);
		((TextView) findViewById(R.id.mainUsername)).setText(profile.getNickname());
	}
	
	private class UpdateCourseTask extends AsyncTask<Void, Void, List<CourseInfo>> {

		@Override
		protected List<CourseInfo> doInBackground(Void... params) {
			return new CourseJsonHandler().handleJsonString(JsonApiCaller.getCourseListApi());
		}
		
		@Override
		protected void onPostExecute(List<CourseInfo> infos) {
			CourseAdapter adapter = new CourseAdapter(CourseActivity.this, infos);
			courseViem.setAdapter(adapter);
		}
	}
	
	private class CourseJsonHandler implements JsonStringHandler<List<CourseInfo>> {

		@Override
		public List<CourseInfo> handleJsonString(String jsonStr) {
			List<CourseInfo> res = new ArrayList<CourseInfo>();
			if (jsonStr == null) {
				return res;
			}
			try {
				JSONArray arr = new JSONArray(jsonStr);
				
				for (int i = 0; i < arr.length(); ++i) {
					CourseInfo info = CourseInfo.load(arr.getJSONObject(i)); 
					if (info == null) {
						Log.w(TAG, "null info returned");
						continue;
					}
					res.add(info);
				}
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
			
			return res;
		}
	}
	
}
