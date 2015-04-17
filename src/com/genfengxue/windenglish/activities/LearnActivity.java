package com.genfengxue.windenglish.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.cache.LessonVideoDownloader;
import com.genfengxue.windenglish.mgr.AccountMgr;
import com.genfengxue.windenglish.network.JsonApiCaller;
import com.genfengxue.windenglish.network.JsonStringHandler;
import com.genfengxue.windenglish.struct.LessonInfo;
import com.genfengxue.windenglish.struct.LessonInfo.LessonState;
import com.genfengxue.windenglish.struct.UserProfile;
import com.genfengxue.windenglish.ui.ConfirmationDialog;
import com.genfengxue.windenglish.ui.ItemsDialog;
import com.genfengxue.windenglish.ui.LessonAdaptor;
import com.genfengxue.windenglish.utils.Constants;
import com.genfengxue.windenglish.utils.UriUtils;

/**
 * Learn Activity
 * 
 * @author Jack Sun(jacksunwei@gmail.com)
 *
 */
public class LearnActivity extends Activity {

	private static final String TAG = "LearnActivity";
	
	private Set<LessonInfo> progress = 
			Collections.newSetFromMap(new WeakHashMap<LessonInfo, Boolean>()); 
	
	private ListView lessonView;
	
	private String[] learnOptions; 

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// init user name bar
		UserProfile userProfile = AccountMgr.getUserProfile(this);
		((TextView) findViewById(R.id.mainUsername)).setText(userProfile.getNickname());

		lessonView = (ListView) findViewById(R.id.videoList);
		learnOptions = getResources().getStringArray(R.array.learn_options);
		
		ImageView refresh = (ImageView) findViewById(R.id.refreshBtn);
		refresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refreshLessionList(true);
				Toast.makeText(LearnActivity.this, 
						R.string.update_lesson, Toast.LENGTH_SHORT).show();
			}
		});
		
		new UpdateTask().execute();
	}
	
	public void onStart() {
		super.onStart();
		// set content of lesson list
		refreshLessionList(false);
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	private void refreshLessionList(boolean forceRefresh) {
		new GetLessonListTask(forceRefresh).execute(2);
	}
	
	private void doDownloadLessonVideo(LessonInfo info) {
		progress.add(info);
		VideoDownloadHandler handler = new VideoDownloadHandler(info, lessonView, progress);
		new Thread(new LessonVideoDownloader
				(info.getCourseNo(), info.getLessonNo(), handler)).start();
	}

	private class LessonItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ListView lv = (ListView) parent;
			Context ctx = lv.getContext();
			LessonInfo info = (LessonInfo) lv.getAdapter().getItem(position);

			if (info.getDownloadState() == LessonState.DOWNLOADED) {
				new ItemsDialog(learnOptions, new LearnOptionsClickListener(info))
					.show(getFragmentManager(), "Learn");
				return;
			}
			
			if (progress.contains(info)) {
				Toast.makeText(ctx, R.string.downloading_video,
						Toast.LENGTH_SHORT).show();
				return;
			}

			new ConfirmationDialog(getResources().getString(R.string.comfirm_download), 
					new DownloadListener(info), 
					ConfirmationDialog.DEAF_LISTENER).show(getFragmentManager(), "Download");
		}

	}

	private class LearnOptionsClickListener implements OnClickListener {

		private LessonInfo info;
		
		public LearnOptionsClickListener(LessonInfo info) {
			this.info = info;
		}
		
		// this method is one-to-one matching R.array.learn_options
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Intent intent = null;
			int courseId = info.getCourseNo();
			int lessonId = info.getLessonNo();
			switch (which) {
			case 0: // 看视频
				intent = new Intent(LearnActivity.this, VideoPlayActivity.class);
				intent.putExtra("courseId", courseId);
				intent.putExtra("lessonId", lessonId);
				intent.putExtra("part", 1);
				LearnActivity.this.startActivity(intent);
				break;
			case 1: // 看中说英
				int learnState = info.getLearnState();
				if (learnState < LessonInfo.WATCH_3_VIDEO) {
					Toast.makeText(LearnActivity.this, R.string.watch_video_hint, Toast.LENGTH_SHORT).show();
				} else {
					intent = new Intent(LearnActivity.this, VideoPlayActivity.class);
					intent.putExtra("courseId", courseId);
					intent.putExtra("lessonId", lessonId);
					intent.putExtra("part", 4);
					startActivity(intent);
				}
				break;
			case 2: // 对答案
				// TODO 
				Toast.makeText(getApplicationContext(), "shit dui daan", Toast.LENGTH_SHORT).show();
				break;
			case 3: // 删除视频
				for (int i = 1; i <= 4; ++i) {
					String path = UriUtils.getLessonVideoPath(courseId, lessonId, i);
					new File(path).delete();
				}
				info.updateState();
				((LessonAdaptor) lessonView.getAdapter()).notifyDataSetChanged();
				Toast.makeText(LearnActivity.this, 
						R.string.delete_video_hint, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
	
	private class GetLessonListTask extends
			AsyncTask<Integer, Void, List<LessonInfo>> {

		private boolean forceRefresh;
		private AndroidHttpClient client = AndroidHttpClient.newInstance("");

		GetLessonListTask(boolean forceRefresh) {
			this.forceRefresh = forceRefresh;
		}
		
		protected void onPreExecute() {
		}

		@Override
		protected List<LessonInfo> doInBackground(Integer... params) {
			// TODO hard code courseNo, should be replaced later
			String jsonStr = JsonApiCaller.getLessonListApi(2, forceRefresh);
			if (jsonStr != null) {
				return new LessonJsonHandler().handleJsonString(jsonStr);
			}

			return new ArrayList<LessonInfo>();
		}

		protected void onPostExecute(List<LessonInfo> result) {
			if (result != null) {
				LessonAdaptor adaptor = new LessonAdaptor(LearnActivity.this, result);
				lessonView.setAdapter(adaptor);
				lessonView.setOnItemClickListener(new LessonItemClickListener());
			}
			if (forceRefresh) {
				Toast.makeText(LearnActivity.this, 
						R.string.update_lesson_finish, Toast.LENGTH_SHORT).show();
			}
			client.close();
		}

	}

	private static class VideoDownloadHandler extends Handler {

		private LessonInfo info;
		private ListView lessonView;
		private Set<LessonInfo> progress;
		
		VideoDownloadHandler(LessonInfo info, ListView lessonView,
				Set<LessonInfo> progress) {
			this.info = info;
			this.lessonView = lessonView;
			this.progress = progress;
		}

		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case LessonVideoDownloader.DOWNLOAD_START:
				info.setDownloadState();
				Toast.makeText(lessonView.getContext(), R.string.downloading_video_start, 
						Toast.LENGTH_SHORT).show();
				updateProgressBar();
				break;
			case LessonVideoDownloader.DOWNLOAD_PROGRESS:
				if (progress.contains(info)) {
					info.setDownloadProgress((Integer) msg.obj);
					progress.add(info);
				}
				updateProgressBar();
				break;
			case LessonVideoDownloader.DOWNLOAD_FINISHED:
				if (progress.contains(info)) {
					info.updateState();
					Toast.makeText(lessonView.getContext(), R.string.downloading_video_finish, 
							Toast.LENGTH_SHORT).show();
					progress.remove(info);
				}
				updateProgressBar();
				break;
			default: // LessonVideoDownloader.DOWNLOAD_FAILED
				String template = lessonView.getResources().getString(
						R.string.downloading_video_failed);
				info.updateState();
				progress.remove(info);
				updateProgressBar();
				Toast.makeText(lessonView.getContext(), String.format(template, info.getLessonNo()),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
		private void updateProgressBar() {
			int id = info.getLessonNo();
			if (id >= lessonView.getFirstVisiblePosition() && id <= lessonView.getLastVisiblePosition()) {
				((LessonAdaptor) lessonView.getAdapter()).notifyDataSetChanged();
			}
		}
	}

	private class LessonJsonHandler implements JsonStringHandler<List<LessonInfo>> {

		@Override
		public List<LessonInfo> handleJsonString(String jsonStr) {
			List<LessonInfo> res = new ArrayList<LessonInfo>();
			SharedPreferences pref = getSharedPreferences(Constants.LESSON_STATE_PREF, MODE_PRIVATE);
			try {
				JSONArray arr = new JSONArray(jsonStr);

				for (int i = 0; i < arr.length(); ++i) {
					JSONObject obj = (JSONObject) arr.get(i);
					int lessonNo = obj.optInt("lessonNo");
					int courseNo = obj.optInt("courseNo");
					res.add(new LessonInfo(lessonNo, courseNo, 
							pref.getInt(LessonInfo.preferenceKey(courseNo, lessonNo), LessonInfo.NOT_LEARNED),
							obj.optString("chineseTitle"),
							obj.optString("englishTitle"), 
							obj.optString("imageUrl")));
				}

				Collections.sort(res, new Comparator<LessonInfo>() {

					@Override
					public int compare(LessonInfo lhs, LessonInfo rhs) {
						return lhs.getLessonNo() - rhs.getLessonNo();
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return res;
		}
	}

	private class DownloadListener implements OnClickListener {

		private LessonInfo info;
		
		public DownloadListener(LessonInfo info) {
			this.info = info;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			ConnectivityManager cm = (ConnectivityManager) 
					getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
				doDownloadLessonVideo(info);
			} else {
				new ConfirmationDialog(getResources().getString(R.string.no_wifi_download),
						new DoDownloadListener(info),
						ConfirmationDialog.DEAF_LISTENER).show(getFragmentManager(), "WIFI");
			}
		}
	}
	
	private class DoDownloadListener implements OnClickListener {
		private LessonInfo info;
		
		public DoDownloadListener(LessonInfo info) {
			this.info = info;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			doDownloadLessonVideo(info);
		}
	}
	
	private class UpdateTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject obj = null;
			String jsonStr = JsonApiCaller.getLastestRelease();
			if (jsonStr != null) {
				try {
					obj = new JSONObject(jsonStr);
				} catch (JSONException e) {
					Log.w(TAG, "failed to parse json: " + e.getMessage());
				}
			}
			return obj;
		}

		protected void onPostExecute(JSONObject obj) {
			if (obj != null) {
				try {
					int newVersionCode = obj.optInt("versionCode", 1);
					int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
					if (newVersionCode > versionCode) {
						String uri = obj.optString("url");
						ConfirmationDialog dialog = new ConfirmationDialog(
								getResources().getString(R.string.new_version_found), 
								new DoUpdateListener(uri), ConfirmationDialog.DEAF_LISTENER);
						dialog.show(getFragmentManager(), "update");
					}
				} catch (NameNotFoundException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
	}
	
	private class DoUpdateListener implements OnClickListener {

		private Uri uri;
		
		DoUpdateListener(String uri) {
			this.uri = Uri.parse(uri);
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
		
	}
	
}
