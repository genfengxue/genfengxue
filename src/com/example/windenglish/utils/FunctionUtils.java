package com.example.windenglish.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
}
