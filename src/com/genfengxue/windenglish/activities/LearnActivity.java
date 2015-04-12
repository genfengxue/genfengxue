package com.genfengxue.windenglish.activities;

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
import android.net.ConnectivityManager;
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
import com.genfengxue.windenglish.network.JsonApiCaller;
import com.genfengxue.windenglish.network.JsonStringHandler;
import com.genfengxue.windenglish.struct.LessonInfo;
import com.genfengxue.windenglish.struct.LessonInfo.LessonState;
import com.genfengxue.windenglish.struct.UserProfile;
import com.genfengxue.windenglish.ui.ConfirmationDialog;
import com.genfengxue.windenglish.ui.ItemsDialog;
import com.genfengxue.windenglish.ui.LessonAdaptor;
import com.genfengxue.windenglish.utils.Constants;
import com.genfengxue.windenglish.utils.FunctionUtils;

/**
 * Learn Activity
 * 
 * @author Jack Sun(jacksunwei@gmail.com)
 *
 */
public class LearnActivity extends Activity {

	private Set<LessonInfo> progress = 
			Collections.newSetFromMap(new WeakHashMap<LessonInfo, Boolean>()); 
	
	private ListView lessonView;
	
	private String[] learnOptions; 

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// TODO should be removed later
		setupApp();

		// init user name bar
		UserProfile userProfile = AccountMgr.getUserProfile();
		((TextView) findViewById(R.id.mainUsername)).setText(userProfile.getNickname());

		lessonView = (ListView) findViewById(R.id.videoList);
		learnOptions = getResources().getStringArray(R.array.learn_options);
		
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

			if (info.getState() != LessonState.UNDOWNLOAD) {
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
			switch (which) {
			case 0: // 看视频
				Intent intent = new Intent(LearnActivity.this, VideoPlayActivity.class);
				intent.putExtra("courseId", info.getCourseId());
				intent.putExtra("lessonId", info.getLessonId());
				intent.putExtra("part", which + 1);
				LearnActivity.this.startActivity(intent);
				break;
			case 1: // 看中说英
				// TODO
				Toast.makeText(getApplicationContext(), "shit zhong ying", Toast.LENGTH_SHORT).show();;
				break;
			case 2: // 对答案
				// TODO 
				Toast.makeText(getApplicationContext(), "shit dui daan", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				// TODO  
				break;
			}
		}
	}
	
	private void doDownloadLessonVideo(LessonInfo info) {
		progress.add(info);
		VideoDownloadHandler handler = new VideoDownloadHandler(info, lessonView, progress);
		new Thread(new LessonVideoDownloader
				(info.getCourseId(), info.getLessonId(), handler)).start();
	}

	private class GetLessonListTask extends
			AsyncTask<Integer, Void, List<LessonInfo>> {

		private AndroidHttpClient client = AndroidHttpClient.newInstance("");

		protected void onPreExecute() {
		}

		@Override
		protected List<LessonInfo> doInBackground(Integer... params) {
			// TODO hard code courseNo, should be replaced later
			String jsonStr = JsonApiCaller.getLessonListApi(2);
			if (jsonStr != null) {
				return new LessonJsonHandler().handleJsonString(jsonStr);
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
					((LessonAdaptor) lessonView.getAdapter()).notifyDataSetChanged();
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
				Toast.makeText(lessonView.getContext(), String.format(template, info.getLessonId()),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
		private void updateProgressBar() {
			int id = info.getLessonId();
			if (id >= lessonView.getFirstVisiblePosition() && id <= lessonView.getLastVisiblePosition()) {
				((LessonAdaptor) lessonView.getAdapter()).notifyDataSetChanged();
			}
		}
	}

	private class LessonJsonHandler implements JsonStringHandler<List<LessonInfo>> {

		@Override
		public List<LessonInfo> handleJsonString(String jsonStr) {
			List<LessonInfo> res = new ArrayList<LessonInfo>();
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
	
	// TODO temp method, should be replaced later
	private void setupApp() {
		if (BuildConfig.DEBUG) {
//			FunctionUtils.deleteFiles(Constants.APP_DIR);
		}
		FunctionUtils.mkdirs(Constants.APP_DIR);
		FunctionUtils.mkdirs(Constants.CACHE_DIR);
		FunctionUtils.mkdirs(Constants.RECORD_DIR);
		FunctionUtils.mkdirs(Constants.VIDEO_DIR);
		FunctionUtils.mkdirs(Constants.USER_DATA_DIR);
	}
}
