package com.genfengxue.windenglish.mgr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.genfengxue.windenglish.network.JsonApiCaller;
import com.genfengxue.windenglish.struct.UserProfile;
import com.genfengxue.windenglish.utils.Constants;

public class AccountMgr {
	
	private static final String TAG = "AccountMgr";
	private static final String USER_DATA_FILE_PATH = Constants.USER_DATA_DIR
			+ "/userdata";
	private static final String MARK_FILED = "withprofile";

	private static UserProfile user = null;
	
	/**
	 * Get the user profile <br>
	 * 
	 * @return current user profile, null if there is not token or token is out
	 *         of date
	 */
	public static UserProfile getUserProfile() {
		if (user == null) {
			File userdata = new File(USER_DATA_FILE_PATH);
			if (!userdata.exists()) {
				return null;
			}
			
			Properties props = new Properties();
			try {
				props.load(new FileInputStream(userdata));
				if ("yes".equals(props.getProperty(MARK_FILED, ""))) {
					user = UserProfile.load(props);
				} else {
					String token = null;
					String jsonStr = null;
					if ((token = props.getProperty("token")) == null || 
							(jsonStr = JsonApiCaller.getUserProfileApi(token)) == null) {
						return null;
					}
					JSONObject obj = new JSONObject(jsonStr);
					props.setProperty("userNo", obj.getString("userNo"));
					props.setProperty("role", obj.getString("role"));
					props.setProperty("nickname", obj.optString("nickname", "no nick name"));
					props.setProperty("avatar", obj.optString("avatar", ""));
					props.setProperty("email", obj.optString("email", "no email"));
					user = UserProfile.load(props);
					
					File tmpFile = new File(userdata.getAbsoluteFile() + "_tmp");
					props.store(new FileWriter(tmpFile), "user data");
					tmpFile.renameTo(userdata);
				}
			} catch (FileNotFoundException e) {
				Log.w(TAG, "user data file load failed: " + e.getMessage());
			} catch (IOException e) {
				Log.w(TAG, "user data file load failed: " + e.getMessage());
			} catch (JSONException e) {
				Log.e(TAG, "user data file load failed: " + e.getMessage());
			}
		}
		
		return user;
	}

	/**
	 * Update the token and store to local file
	 * 
	 * @param userNo
	 *            user number
	 * @param password
	 *            password
	 * @return true if successfully require token, false otherwise
	 */
	public static boolean updateToken(int userNo, String password) {
		String jsonStr = JsonApiCaller.postTokenApi(userNo, password);
		try {
			JSONObject obj = new JSONObject(jsonStr);
			
			String token = obj.getString("token");
			if (token == null) {
				Log.e(TAG, "access denied, no token response by server");
				return false;
			}
			
			File userdata = new File(USER_DATA_FILE_PATH);
			userdata.delete();
			File tmpfile = new File(USER_DATA_FILE_PATH + "_tmp");
			Properties props = new Properties();
			props.put("token", token);
			props.put("userNo", String.valueOf(userNo));
			props.put("password", password);
			props.put(MARK_FILED, "no");
			props.store(new FileWriter(tmpfile), "user data");
			tmpfile.renameTo(userdata);
			return true;
		} catch (JSONException e) {
			Log.e(TAG, "token update failed: " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "token update failed: " + e.getMessage());
		}
		
		return false;
	}
}
