package com.genfengxue.windenglish.utils;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class UriUtils {

	private static final String LESSON_URI_TEMPLATE = 
			"http://7u2qm8.com1.z0.glb.clouddn.com/video%d_%d.mp4";

	private static final String LESSON_PATH_TEMPLATE = Constants.VIDEO_DIR
			+ "/%d_%d_%d.mp4";

	public static String getLessonVideoUri(int courseId, int lessonId, int part) {
		return String.format(LESSON_URI_TEMPLATE, lessonId, part);
	}

	public static String getLessonVideoPath(int courseId, int lessonId, int part) {
		return String.format(LESSON_PATH_TEMPLATE, courseId, lessonId, part);
	}
}
