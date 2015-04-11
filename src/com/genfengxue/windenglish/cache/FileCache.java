package com.genfengxue.windenglish.cache;

import java.io.File;

import com.genfengxue.windenglish.utils.Constants;
import com.genfengxue.windenglish.utils.FunctionUtils;

public class FileCache {

	/**
	 * Get the local file path of cached uri
	 * 
	 * @param uri
	 *            the uri of resource
	 * @return file path of the uri, null if not cached
	 */
	public static String getCacheFilePath(String uri) {
		return Constants.CACHE_DIR + "/" + FunctionUtils.sha1(uri);
	}

	/**
	 * Get the local file of cached uri, do not guarantee the existence of the
	 * file
	 * 
	 * @param uri
	 * @return
	 */
	public static File getCacheFile(String uri) {
		return new File(getCacheFilePath(uri));
	}
}
