package com.genfengxue.windenglish.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.utils.UriUtils;

public class VideoPlayActivity extends Activity {

	private VideoView videoView;
	private MediaController controller;
	
	private TextView infoTextView;
	private Button startVideoBtn;

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
		controller.setEnabled(false);
		
		videoView.setMediaController(controller);
		videoView.setVideoPath(
				UriUtils.getLessonVideoPath(courseId, lessonId, part));
		
		videoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				controller.setEnabled(true);
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
					VideoPlayActivity.this.startActivity(intent);
					VideoPlayActivity.this.finish();
				}
			}
		});
	}
	
	private class StartVideoBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			infoTextView.setVisibility(View.INVISIBLE);
			startVideoBtn.setEnabled(false);
			startVideoBtn.setVisibility(View.INVISIBLE);
			videoView.start();
		} 
	}
}
