package com.genfengxue.windenglish.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.genfengxue.windenglish.cache.FileCache;
import com.genfengxue.windenglish.utils.Constants;

public class JsonApiCaller {
	private static final String TAG = "JsonApiCaller";
	
	/**
	 * Get api result, should not be called from main thread
	 * 
	 * @param url
	 *            url of api
	 * @return content of response, null if url is not available
	 */
	public static String getApiContent(String urlStr, boolean forceRefresh) {
		try {
			File cacheFile = FileCache.getCacheFile(urlStr);
			if (!forceRefresh && cacheFile.exists()) {
				StringBuilder sb = new StringBuilder();
				BufferedReader br = new BufferedReader(new FileReader(cacheFile));
				String str = null;

				while ((str = br.readLine()) != null) {
					sb.append(str).append("\n");
				}

				br.close();
				return sb.toString();
			}

			URL url = new URL(urlStr);
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(url.openStream()));
			File tmpFile = new File(cacheFile.getAbsoluteFile() + "_tmp");
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
			String str = null;

			while ((str = br.readLine()) != null) {
				sb.append(str).append("\n");
				bw.write(str);
				bw.newLine();
			}

			bw.flush();
			bw.close();
			br.close();
			tmpFile.renameTo(cacheFile);
			return sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * POST API with GET-like Parameters <br>
	 * post api is never cached, and parameter is like GET requests
	 * 
	 * @param urlStr
	 *            string of url
	 * @param params
	 *            parameter string
	 * @return string of response; null is not available
	 */
	public static String postApiContent(String urlStr, String params) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Accept", "application/json");
			
			conn.setDoOutput(true);
			conn.getOutputStream().write(params.getBytes());
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
			
			int code = 0;
			if ((code = conn.getResponseCode()) == 200) {
				StringBuilder sb = new StringBuilder();
				String str = null;
				BufferedReader br = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				
				while ((str = br.readLine()) != null) {
					sb.append(str).append("\n");
				}
				
				br.close();
				return sb.toString();
			} else {
				Log.w(TAG, "post request failed with code: " + code);
				return null;
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "post request with exception: " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "post request with exception: " + e.getMessage());
		}

		return null;
	}

	public static String getLessonListApi(int courseNo, boolean forceRefresh) {
		QueryBuilder qb = new QueryBuilder();
		qb.addIntQuery("courseNo", courseNo);
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.LESSON_LIST_API_URI).append("?")
				.append(qb.getQuery());
		return getApiContent(sb.toString(), forceRefresh);
	}
	
	public static String getCourseListApi() {
		return getApiContent(Constants.COURSE_LIST_API_URI, false);
	}

	public static String getLessonListApi(int courseNo) {
		return getLessonListApi(courseNo, false);
	}
	
	public static String buildGetUrl(String baseUrl, QueryBuilder qb) {
		return baseUrl + "?" + qb.getQuery();
	}
	
	public static String getUserProfileApi(String token) {
		QueryBuilder qb = new QueryBuilder();
		qb.addStringQuery("access_token", token);
		String urlStr = buildGetUrl(Constants.USER_PROFILE_URI, qb);
		return getApiContent(urlStr, true);
	}

	/**
	 * Get user token with post api
	 * 
	 * @param userNo
	 * @param password
	 * @return
	 */
	public static String postTokenApi(int userNo, String password) {
		QueryBuilder builder = new QueryBuilder();
		builder.addIntQuery("userNo", userNo);
		builder.addStringQuery("password", password);
		String uri = Constants.GET_TOKEN_URI;
		return postApiContent(uri, builder.getQuery());
	}
	
	public static String postUpdateProfile(String token, String email, String nickname) {
		QueryBuilder qbParam = new QueryBuilder();
		qbParam.addStringQuery("access_token", token);
		String urlStr = Constants.UPDATE_PROFILE_URI + "?" + qbParam.getQuery();
		QueryBuilder body = new QueryBuilder();
		if (email != null)
			body.addStringQuery("email", email);
		if (nickname != null)
			body.addStringQuery("nickname", nickname);
		return postApiContent(urlStr, body.getQuery());
	}
	
	public static String getLastestRelease() {
		return getApiContent(Constants.GET_LATEST_RELEASE_URI, true);
	}
}
