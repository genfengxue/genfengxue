package com.genfengxue.windenglish.cache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.genfengxue.windenglish.utils.FunctionUtils;
import com.genfengxue.windenglish.utils.UriUtils;

public class LessonVideoDownloader implements Runnable {

	private static final String TAG = "LessonVideoDownloader";
	
	private int courseId, lessonId;
	private Handler handler;

	public static final int DOWNLOAD_START = 0;
	public static final int DOWNLOAD_PROGRESS = 1;
	public static final int DOWNLOAD_FINISHED = 2;
	public static final int DOWNLOAD_FAILED = 3;

	private static final int VIDEO_PART_NUM = 4;

	public LessonVideoDownloader(int courseId, int lessonId,
			Handler progressHandler) {
		this.lessonId = lessonId;
		this.handler = progressHandler;
	}

	@Override
	public void run() {
		handler.sendEmptyMessage(DOWNLOAD_START);

		for (int part = 1; part <= VIDEO_PART_NUM; ++part) {
			String path = UriUtils.getLessonVideoPath(courseId, lessonId, part);
			File file = new File(path);
			if (!file.exists()) {
				try {
					File tmpFile = new File(path + "_tmp");

					AndroidHttpClient client = AndroidHttpClient.newInstance("Mozilla/5.0");
					HttpGet get = new HttpGet(
							UriUtils.getLessonVideoUri(courseId, lessonId, part));
					HttpResponse response = client.execute(get);
					
					if (200 == response.getStatusLine().getStatusCode()) {
						FunctionUtils.pipeIo(response.getEntity().getContent(), 
								new BufferedOutputStream(new FileOutputStream(tmpFile)));;
						client.close();
						Log.i(TAG, "downloaded " + path);
						tmpFile.renameTo(file);
					} else {
						Log.e(TAG, "fail to request url");
						client.close();
						handler.sendEmptyMessage(DOWNLOAD_FAILED);
						return;
					}
				} catch (IOException e) {
					Log.e(TAG, "download failed for " + e.getMessage());
					handler.sendEmptyMessage(DOWNLOAD_FAILED);
					return;
				}
				
			}

			Message msg = handler.obtainMessage(DOWNLOAD_PROGRESS, 
					part * 100 / VIDEO_PART_NUM);
			handler.sendMessage(msg);
		}

		handler.sendEmptyMessage(DOWNLOAD_FINISHED);
	}

}
