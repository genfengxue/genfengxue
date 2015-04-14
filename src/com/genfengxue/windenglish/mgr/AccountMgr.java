package com.genfengxue.windenglish.mgr;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.genfengxue.windenglish.network.JsonApiCaller;
import com.genfengxue.windenglish.struct.UserProfile;
import com.genfengxue.windenglish.utils.Constants;

public class AccountMgr {
	
	private static final String TAG = "AccountMgr";
	private static final String MARK_FILED = "withprofile";

	private static UserProfile user = null;
	
	/**
	 * Get the user profile <br>
	 * 
	 * @return current user profile, null if there is not token or token is out
	 *         of date
	 */
	public static UserProfile getUserProfile(Context ctx) {
		if (user == null) {
			SharedPreferences userdata = ctx.getSharedPreferences(
					Constants.USER_PROFILE_PREF, Context.MODE_PRIVATE);
			if (!userdata.contains("token")) {
				return null;
			}

			if (userdata.contains(MARK_FILED)) {
				return UserProfile.load(userdata);
			} else {
				String token = userdata.getString("token", "");
				String jsonStr = null;
				if ((jsonStr = JsonApiCaller.getUserProfileApi(token)) == null) {
					return null;
				}
				
				try {
					JSONObject obj = new JSONObject(jsonStr);
					Editor editor = userdata.edit();
					editor.putInt("userNo", obj.optInt("userNo"));
					editor.putInt("role", obj.optInt("role"));
					editor.putString("nickname", obj.optString("nickname", "Student"));
					editor.putString("avatar", obj.optString("avatar", ""));
					editor.putString("email", obj.optString("email"));
					editor.putString(MARK_FILED, "");
					editor.commit();
					user = UserProfile.load(userdata);
				} catch (JSONException e) {
					Log.e(TAG, "json format corrupted: " + e.getMessage());
					return null;
				}
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
	public static boolean updateToken(Context ctx, int userNo, String password) {
		String jsonStr = JsonApiCaller.postTokenApi(userNo, password);
		if (jsonStr == null) {
			return false;
		}
		try {
			JSONObject obj = new JSONObject(jsonStr);
			
			String token = obj.optString("token");
			if ("".equals(token)) {
				Log.e(TAG, "access denied, no token response by server");
				return false;
			}

			SharedPreferences.Editor editor = ctx.getSharedPreferences(
					Constants.USER_PROFILE_PREF, Context.MODE_PRIVATE).edit();
			
			editor.clear();
			editor.putString("token", token);
			editor.putString("userNo", String.valueOf(userNo));
			editor.putString("password", password);
			editor.commit();

			return true;
		} catch (JSONException e) {
			Log.e(TAG, "token update failed: " + e.getMessage());
		}
		return false;
	}
}
