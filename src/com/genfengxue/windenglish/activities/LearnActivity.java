package com.genfengxue.windenglish.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.genfengxue.windenglish.BuildConfig;
import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.cache.LessonVideoDownloader;
import com.genfengxue.windenglish.mgr.AccountMgr;
import com.genfengxue.windenglish.struct.LessonInfo;
import com.genfengxue.windenglish.struct.UserProfile;
import com.genfengxue.windenglish.ui.LessonAdaptor;
import com.genfengxue.windenglish.utils.Constants;
import com.genfengxue.windenglish.utils.FunctionUtils;

public class LearnActivity extends Activity {

	private Map<LessonInfo, Integer> progress = 
			new WeakHashMap<LessonInfo, Integer>();
	
	private ListView lessonView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// TODO should be removed later
		setupApp();

		// init user name bar
		UserProfile user = AccountMgr.getUserProfile();
		((TextView) findViewById(R.id.mainUsername)).setText(user.getName());

		lessonView = (ListView) findViewById(R.id.videoList);
		
		// set content of lesson list
		new GetLessonListTask().execute(2);
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
			Context ctx = lv.getContext();
			LessonInfo info = (LessonInfo) lv.getAdapter().getItem(position);

			if (progress.containsKey(info)) {
				Toast.makeText(ctx, R.string.downloading_video,
						Toast.LENGTH_SHORT).show();
				return;
			}

			// TODO
			// ensure download
			// check wifi
			// download
			VideoDownloadHandler handler = new VideoDownloadHandler(info, lessonView, progress);
			new Thread(new LessonVideoDownloader
					(info.getCourseId(), info.getLessonId(), handler)).start();

			Toast.makeText(lv.getContext(), info.toString(), Toast.LENGTH_SHORT)
					.show();
		}

	}

	private class GetLessonListTask extends
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
				LessonAdaptor adaptor = new LessonAdaptor(lessonView, result);
				lessonView.setAdapter(adaptor);
				lessonView.setOnItemClickListener(new LessonItemClickListener());
			}
			client.close();
		}

	}

	private static class VideoDownloadHandler extends Handler {

		private LessonInfo info;
		private ListView lessonView;
		private Map<LessonInfo, Integer> progress;
		
		VideoDownloadHandler(LessonInfo info, ListView lessonView,
				Map<LessonInfo, Integer> progress) {
			this.info = info;
			this.lessonView = lessonView;
			this.progress = progress;
		}

		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case LessonVideoDownloader.DOWNLOAD_START:
				Toast.makeText(lessonView.getContext(), R.string.downloading_video_start, 
						Toast.LENGTH_SHORT).show();;
				break;
			case LessonVideoDownloader.DOWNLOAD_PROGRESS:
				if (progress.containsKey(info)) {
					progress.put(info, (Integer) msg.obj);
					// TODO find a method to update progress bar
				}
				break;
			case LessonVideoDownloader.DOWNLOAD_FINISHED:
				if (progress.containsKey(info)) {
					Toast.makeText(lessonView.getContext(), R.string.downloading_video_finish, 
							Toast.LENGTH_SHORT).show();
					progress.remove(info);
				}
				break;
			default: // LessonVideoDownloader.DOWNLOAD_FAILED
				String template = lessonView.getResources().getString(
						R.string.downloading_video_failed);
				Toast.makeText(lessonView.getContext(), String.format(template, info.getCourseId()),
						Toast.LENGTH_SHORT).show();
				progress.remove(info);
				break;
			}
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

					res.add(new LessonInfo(obj.getInt("lessonNo"), obj
							.getInt("courseNo"), obj.getString("chineseTitle"),
							obj.getString("englishTitle"), obj
									.getString("imageUrl")));
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
		if (BuildConfig.DEBUG) {
			FunctionUtils.deleteFiles(Constants.APP_DIR);
		}
		FunctionUtils.mkdirs(Constants.APP_DIR);
		FunctionUtils.mkdirs(Constants.CACHE_DIR);
		FunctionUtils.mkdirs(Constants.Record_DIR);
		FunctionUtils.mkdirs(Constants.VIDEO_DIR);
		Constants.MAIN_CONTEXT = this;
	}
}
