package com.genfengxue.windenglish.utils;

import android.content.Context;
import android.os.Environment;

public class Constants {

	public static final String APP_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/GenFengXue";

	public static final String CACHE_DIR = APP_DIR + "/Cache";
	
	public static final String VIDEO_DIR = APP_DIR + "/Video";
	
	public static final String RECORD_DIR = APP_DIR + "/Record";
	
	public static final String LESSON_LIST_API_URI = 
			"http://data.genfengxue.com/api/lessons?course";
	
	// should be initialized at the start of programming 
	public static Context MAIN_CONTEXT;
}
