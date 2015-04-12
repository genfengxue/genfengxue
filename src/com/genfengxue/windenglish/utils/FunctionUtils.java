package com.genfengxue.windenglish.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.genfengxue.windenglish.BuildConfig;

public class FunctionUtils {
	public static String sha1(String input) {
		try {
			MessageDigest mDigest;
			mDigest = MessageDigest.getInstance("SHA1");
			byte[] result = mDigest.digest(input.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16)
						.substring(1));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static void mkdirs(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static void deleteFiles(String path) {
		deleteFiles(new File(path));
	}
	
	public static void deleteFiles(File dir) {
		if (dir.exists()) {
			if (dir.isFile()) {
				dir.delete();
			} else if (dir.isDirectory()) {
				for (File f : dir.listFiles()) {
					deleteFiles(f);
				}
			}
		}

	}
	
	public static void setupApp() {
		if (BuildConfig.DEBUG) {
//			FunctionUtils.deleteFiles(Constants.APP_DIR);
		}
		FunctionUtils.mkdirs(Constants.APP_DIR);
		FunctionUtils.mkdirs(Constants.CACHE_DIR);
		FunctionUtils.mkdirs(Constants.RECORD_DIR);
		FunctionUtils.mkdirs(Constants.VIDEO_DIR);
		FunctionUtils.mkdirs(Constants.USER_DATA_DIR);
	}
}
