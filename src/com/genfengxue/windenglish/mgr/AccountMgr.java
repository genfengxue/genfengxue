package com.genfengxue.windenglish.mgr;

import com.genfengxue.windenglish.struct.UserProfile;

public class AccountMgr {

	private static UserProfile user = null;

	// TODO 
	public static UserProfile getUserProfile() {
		if (user == null) {
			user = new UserProfile();
			user.setUserName("Jack");
			user.setName("SJ");
			user.setEmail("sss@sss");
		}
		return user;
	}
}
