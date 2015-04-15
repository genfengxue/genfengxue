package com.genfengxue.windenglish.activities;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.struct.LessonInfo;
import com.genfengxue.windenglish.ui.ConfirmationDialog;
import com.genfengxue.windenglish.utils.Constants;
import com.genfengxue.windenglish.utils.UriUtils;

public class VideoPlayActivity extends Activity {

	private static final String TAG = "VideoPlayActivity";
	
	private VideoView videoView;
	private MediaController controller;
	
	private TextView infoTextView;
	private Button startVideoBtn;
	
	// represent whether user finish his record
	private boolean recordFinished = false;
	private MediaRecorder recorder;
	private String recordPath;

	private int courseId, lessonId, part;
	
	private String[] infoArr;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);
		
		infoArr = getResources().getStringArray(R.array.video_info_arr);
		startVideoBtn = (Button) findViewById(R.id.start_video_btn);
		startVideoBtn.setEnabled(false);
		startVideoBtn.setOnClickListener(new StartVideoBtnListener());
		infoTextView = (TextView) findViewById(R.id.part_info);
		
		Intent intent = getIntent();
		courseId = intent.getIntExtra("courseId", 1);
		lessonId = intent.getIntExtra("lessonId", 1);
		part = intent.getIntExtra("part", 1);
		infoTextView.setText(infoArr[part - 1]);
		
		videoView = (VideoView) findViewById(R.id.videoView);
		controller = new MediaController(this, false);
		
		// TODO controller will be removed in later development
		// we can have our own controller style
		videoView.setMediaController(controller);
		videoView.setVideoPath(
				UriUtils.getLessonVideoPath(courseId, lessonId, part));
		
		videoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				startVideoBtn.setEnabled(true);
			}
		});
		
		videoView.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (part != infoArr.length) {
					Intent intent = new Intent(VideoPlayActivity.this, VideoPlayActivity.class);
					intent.putExtra("courseId", courseId);
					intent.putExtra("lessonId", lessonId);
					intent.putExtra("part", part + 1);
					updateLearnState(part);
					VideoPlayActivity.this.startActivity(intent);
					VideoPlayActivity.this.finish();
				} else {
					recordFinished = true;
					stopRecording();
					updateLearnState(part);
					infoTextView.setText(R.string.record_finish);
					infoTextView.setVisibility(View.VISIBLE);
					startVideoBtn.setText(R.string.check_answer);
					startVideoBtn.setVisibility(View.VISIBLE);
					startVideoBtn.setEnabled(true);
				}
			}
		});
	}
	
	private class StartVideoBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!recordFinished) {
				infoTextView.setVisibility(View.INVISIBLE);
				startVideoBtn.setEnabled(false);
				startVideoBtn.setVisibility(View.INVISIBLE);
				
				if (part == infoArr.length)
					startRecording();
				videoView.start();
			} else {
				// TODO
				Toast.makeText(VideoPlayActivity.this, "Go Check Answer", Toast.LENGTH_SHORT).show();
			}
		} 
	}
	
	@SuppressLint("InlinedApi")
	protected void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		}
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	public void onBackPressed() {
		if (part < infoArr.length) {
			new ConfirmationDialog(
					getResources().getString(R.string.video_exit_alert), 
					getResources().getString(R.string.confirm_exit), 
					new ExitListener(), null, null,
					getResources().getString(R.string.continue_watch),
					ConfirmationDialog.DEAF_LISTENER).show(getFragmentManager(), "Confirm");
		} else if (!recordFinished) {
			new ConfirmationDialog(
					getResources().getString(R.string.record_exit_alert), 
					getResources().getString(R.string.confirm_exit), new ExitListener(), 
					null, null, 
					getResources().getString(R.string.continue_record), 
					ConfirmationDialog.DEAF_LISTENER).show(getFragmentManager(), "Record");
		} else {
			super.onBackPressed();
		}
	}
	
	public class ExitListener implements android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			cleanAndExit();
		}
	}

	private void cleanAndExit() {
		// TODO release media info
		if (part == infoArr.length)
			stopRecording();
		finish();
	}
	
	private void startRecording() {
		if (recordPath == null) {
			recordPath = UriUtils.getRecordPath(courseId, lessonId);
		}
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setOutputFile(recordPath);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		try {
			recorder.prepare();
		} catch (IOException e) {
			Log.e(TAG, "recorder cannot prepared.");
		}
		
		recorder.start();
		ImageView iv = (ImageView) findViewById(R.id.record_icon);
		iv.setVisibility(View.VISIBLE);
	}
	
	private void stopRecording() {
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
	}
	
	private void updateLearnState(int part) {
		Editor editor = getSharedPreferences(
				Constants.LESSON_STATE_PREF, MODE_PRIVATE).edit();
		String key = LessonInfo.preferenceKey(courseId, lessonId);
		switch (part) {
		case 1:
			editor.putInt(key, LessonInfo.WATCH_1_VIDEO);
			break;
		case 2:
			editor.putInt(key, LessonInfo.WATCH_2_VIDEO);
			break;
		case 3:
			editor.putInt(key, LessonInfo.WATCH_3_VIDEO);
			break;
		case 4:
			editor.putInt(key, LessonInfo.RECORDED);
		default:
			break;
		}
		editor.commit();
	}
}
