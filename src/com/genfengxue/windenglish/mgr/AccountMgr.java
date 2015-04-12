package com.genfengxue.windenglish.mgr;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.genfengxue.windenglish.struct.UserProfile;
import com.genfengxue.windenglish.utils.FileUtils;

public class AccountMgr {

	private static UserProfile userProfile = null;

	/**
	 * Get the user profile <br>
	 * 
	 * @return current user profile, null if there is not token or token is out
	 *         of date
	 */
	public static UserProfile getUserProfile() {
		if (userProfile == null) {
			String userDataString = FileUtils.readUserData();
			if (userDataString == null) return null;
			
			JSONObject userData = null;
			try {
				userData = new JSONObject(userDataString);
			} catch (JSONException e) {
				Log.e("wind", "user data json parse error");
				e.printStackTrace();
				return null;
			}
			
			userProfile = new UserProfile(
				userData.optInt("userNo"),
				userData.optInt("role"),
				userData.optString("accessToken"),
				userData.optString("nickname"),
				userData.optString("avatar"),
				userData.optString("email")
			);
		}
		return userProfile;
	}

	/**
	 * Update the token and store to local file
	 * 
	 * @param userNo
	 *            user number
	 * @param password
	 *            password
	 */
	public static void updateToken(int userNo, String password) {
		// update token 
		// delete the existing profile
	}
}
