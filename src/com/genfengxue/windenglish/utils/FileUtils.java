package com.genfengxue.windenglish.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class FileUtils {
	
	public static final String TAG = FileUtils.class.getSimpleName();

	public static void pipeIo(InputStream is, OutputStream os) throws IOException {
		pipeIo(is, os, null);
	}
	
	public static void pipeIo(InputStream is, OutputStream os, ProgressUpdater updater)
			throws IOException {
		byte[] buffer = new byte[1024];
		int length = 0;
		int count = 0;
		if (updater != null) {
			while ((length = is.read(buffer)) != -1) {
				os.write(buffer, 0, length);
				updater.update(count += length);
			}
		} else {
			while ((length = is.read(buffer)) != -1) {
				os.write(buffer, 0, length);
			}
		}

		is.close();
		os.flush();
		os.close();
	}

	public interface ProgressUpdater {
		void update(int byteNum);
	}
	
	public static void writeFile(String path, String content) {
		FileWriter fw;
		try {
			fw = new FileWriter(path);
			try {
				fw.write(content);
			} catch (Exception e) {
				Log.e(TAG, path + " write content error: " + content);
				e.printStackTrace();
			} finally {
				fw.close();
			}
		} catch (IOException e) {
			Log.e(TAG, path + " error");
			e.printStackTrace();
		}
	}
	
	public static String readFile(String path) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			
			String str = null;
			while ((str = br.readLine()) != null) {
				sb.append(str).append("\n");
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			Log.e("wind", path + " not found");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Log.e("wind", path + " io exception");
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}
}
