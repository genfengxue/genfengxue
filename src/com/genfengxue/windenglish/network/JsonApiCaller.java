package com.genfengxue.windenglish.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.genfengxue.windenglish.cache.FileCache;
import com.genfengxue.windenglish.utils.Constants;

public class JsonApiCaller {
	/**
	 * Get api result, should not be called from main thread
	 * 
	 * @param url
	 *            url of api
	 * @return content of response, null if url is not available
	 */
	public static String getApiContent(String urlStr, boolean refresh)  {
		try {
			File cacheFile = FileCache.getCacheFile(urlStr);
			if (!refresh && cacheFile.exists()) {
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
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
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
	
	public static String getLessonListApi(int courseNo, boolean refresh) {
		QueryBuilder qb = new QueryBuilder();
		qb.addIntQuery("courseNo", courseNo);
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.LESSON_LIST_API_URI).append("?").append(qb.getQuery());
		return getApiContent(sb.toString(), refresh);
	}
	
	public static String getLessonListApi(int courseNo) {
		return getLessonListApi(courseNo, true);
	}
}
