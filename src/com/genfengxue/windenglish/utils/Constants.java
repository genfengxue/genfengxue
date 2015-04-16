package com.genfengxue.windenglish.utils;

import android.os.Environment;

public class Constants {

	public static final String APP_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/GenFengXue";

	public static final String CACHE_DIR = APP_DIR + "/Cache";
	
	public static final String VIDEO_DIR = APP_DIR + "/Video";
	
	public static final String RECORD_DIR = APP_DIR + "/Record";
	
	public static final String API_BASE_URI = "http://data.genfengxue.com/api";
	
	public static final String LESSON_LIST_API_URI = API_BASE_URI + 
			"/lessons";
	
	/**
	 *  use http post to get the token
	 */
	public static final String GET_TOKEN_URI = API_BASE_URI + "/auth/local";
	
	public static final String TOKEN_VALIDATE_URI = API_BASE_URI + "/auth";
	
	public static final String USER_PROFILE_URI = API_BASE_URI + "/users/me";
	
	public static final String CHANGE_PASSWORD_URI = API_BASE_URI + 
			"/users/change_password";

	public static final String UPDATE_PROFILE_URI = API_BASE_URI + 
			"/users/update_profile";
	
	public static final String APP_NAME = "com.genfengxue.windenglish";
	
	public static final String USER_PROFILE_PREF = APP_NAME + ".userprofile";
	
	public static final String LESSON_STATE_PREF = APP_NAME + ".lessonstate";
}
