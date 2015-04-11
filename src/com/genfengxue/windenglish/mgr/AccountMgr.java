package com.genfengxue.windenglish.mgr;

import com.genfengxue.windenglish.struct.UserProfile;

public class AccountMgr {

	private static UserProfile user = null;

	/**
	 * Get the user profile <br>
	 * 
	 * @return current user profile, null if there is not token or token is out
	 *         of date
	 */
	public static UserProfile getUserProfile() {
		if (user == null) {
			// TODO add logic
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
	 */
	public static void updateToken(int userNo, String password) {
		// update token 
		// delete the existing profile
	}
}
