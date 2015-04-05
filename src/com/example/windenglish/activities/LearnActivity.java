package com.example.windenglish.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.windenglish.R;
import com.example.windenglish.mgr.AccountMgr;
import com.example.windenglish.struct.LessonInfo;
import com.example.windenglish.struct.UserProfile;
import com.example.windenglish.ui.LessonAdaptor;
import com.example.windenglish.utils.Constants;
import com.example.windenglish.utils.FunctionUtils;

public class LearnActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// TODO should be removed later
		setupApp();

		// init user name bar
		UserProfile user = AccountMgr.getUserProfile();
		((TextView) findViewById(R.id.mainUsername)).setText(user.getName());

		// set content of lesson list
		new LessonListGetTask().execute(2);
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private class LessonItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ListView lv = (ListView) parent;
			LessonInfo info = (LessonInfo) lv.getAdapter().getItem(position);

			Toast.makeText(lv.getContext(), info.toString(), Toast.LENGTH_SHORT)
					.show();
		}

	}

	private class LessonListGetTask extends
			AsyncTask<Integer, Void, List<LessonInfo>> {

		private AndroidHttpClient client = AndroidHttpClient.newInstance("");

		protected void onPreExecute() {
		}

		@Override
		protected List<LessonInfo> doInBackground(Integer... params) {
			HttpGet get = new HttpGet(Constants.LESSON_LIST_API_URI);
			try {
				return client.execute(get, new LessonJsonHandler());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return new ArrayList<LessonInfo>();
		}

		protected void onPostExecute(List<LessonInfo> result) {
			if (result != null) {
				ListView lv = (ListView) findViewById(R.id.videoList);
				LessonAdaptor adaptor = new LessonAdaptor(lv.getContext(),
						R.layout.videolistitem, R.id.ItemTitle, result);
				lv.setAdapter(adaptor);
				lv.setOnItemClickListener(new LessonItemClickListener());
			}
			if (null != client)
				client.close();
		}

	}

	private class LessonJsonHandler implements
			ResponseHandler<List<LessonInfo>> {

		@Override
		public List<LessonInfo> handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			List<LessonInfo> res = new ArrayList<LessonInfo>();
			String jsonStr = new BasicResponseHandler()
					.handleResponse(response);

			try {
				JSONArray arr = new JSONArray(jsonStr);

				for (int i = 0; i < arr.length(); ++i) {
					JSONObject obj = (JSONObject) arr.get(i);

					res.add(new LessonInfo(obj.getInt("lessonNo"), 
							obj.getInt("courseNo"), obj.getString("chineseTitle"),
							obj.getString("englishTitle"), obj.getString("imageUrl")));
				}

				Collections.sort(res, new Comparator<LessonInfo>() {

					@Override
					public int compare(LessonInfo lhs, LessonInfo rhs) {
						return lhs.getLessonId() - rhs.getLessonId();
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return res;
		}
	}
	
	// TODO temp method, should be replaced later
	private void setupApp() {
		File dir = new File(Constants.APP_DIR);
		FunctionUtils.deleteFiles(dir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		File cache = new File(Constants.CACHE_DIR);
		if (!cache.exists()) {
			cache.mkdirs();
		}
		
	}

}
