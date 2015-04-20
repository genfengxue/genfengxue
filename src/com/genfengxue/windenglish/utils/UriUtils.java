package com.genfengxue.windenglish.utils;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class UriUtils {

	private static final String LESSON_URI_TEMPLATE = 
			"http://7u2qm8.com1.z0.glb.clouddn.com/%d_%d_%d.mp4";

	private static final String LESSON_PATH_TEMPLATE = Constants.VIDEO_DIR
			+ "/%d_%d_%d.mp4";

	private static final String RECORD_PATH_TEMPLATE = Constants.RECORD_DIR
			+ "/%d_%d.3gp";

	private static final String LIKED_PATH_TEMPLATE = Constants.LIKED_DIR
			+ "/%d_%d.json";

	public static String getLessonVideoUri(int courseNo, int lessonNo, int part) {
		return String.format(LESSON_URI_TEMPLATE, courseNo, lessonNo, part);
	}

	public static String getLessonVideoPath(int courseNo, int lessonNo, int part) {
		return String.format(LESSON_PATH_TEMPLATE, courseNo, lessonNo, part);
	}
	
	public static String getRecordPath(int courseNo, int lessonNo) {
		return String.format(RECORD_PATH_TEMPLATE, courseNo, lessonNo);
	}
	
	public static String getLikedPath(int courseNo, int lessonNo) {
		return String.format(LIKED_PATH_TEMPLATE, courseNo, lessonNo);
	}
}
