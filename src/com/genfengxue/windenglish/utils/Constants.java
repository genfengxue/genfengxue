package com.genfengxue.windenglish.utils;

import android.os.Environment;

public class Constants {

	public static final String APP_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/GenFengXue";

	public static final String CACHE_DIR = APP_DIR + "/Cache";
	
	public static final String VIDEO_DIR = APP_DIR + "/Video";
	
	public static final String RECORD_DIR = APP_DIR + "/Record";
	
	public static final String USER_DATA_DIR = APP_DIR + "/User";
	
	public static final String LESSON_LIST_API_URI = 
			"http://data.genfengxue.com/api/lessons";
	
}
