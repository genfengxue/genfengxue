package com.genfengxue.windenglish.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	public static void pipeIo(InputStream is, OutputStream os)
			throws IOException {
		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = is.read(buffer)) != -1) {
			os.write(buffer, 0, length);
		}

		is.close();
		os.close();
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
