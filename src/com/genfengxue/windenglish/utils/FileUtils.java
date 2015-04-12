package com.genfengxue.windenglish.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.util.Log;

public class FileUtils {
	public static String readUserData() {
		return readFile(UriUtils.getUserDataPath());
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
