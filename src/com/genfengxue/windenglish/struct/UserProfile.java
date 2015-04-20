package com.genfengxue.windenglish.struct;

import android.content.SharedPreferences;

public class UserProfile {
	public static final int USER_ROLE_STUDENT = 1;
	public static final int USER_ROLE_TEACHER = 2;
	public static final int USER_ROLE_ADMIN = 3;

	private int userNo;
	private int role;
	private String nickname;
	private String avatar;
	private String email;

	private UserProfile(int userNo, int role, String nickName, String avatar,
			String email) {
		this.userNo = userNo;
		this.role = role;
		this.nickname = nickName;
		this.avatar = avatar;
		this.email = email;
	}
	
	public static UserProfile load(SharedPreferences pref) {
		int userNo = pref.getInt("userNo", 0);
		int role = pref.getInt("role", USER_ROLE_STUDENT);
		String nickname = pref.getString("nickname", "User: " + userNo);
		String avatar = pref.getString("avatar", "");
		String email = pref.getString("email", "");
		return new UserProfile(userNo, role, nickname, avatar, email);
	}

	public String getNickname() {
		return nickname;
	}

	public int getUserNo() {
		return userNo;
	}

	public String getEmail() {
		return email;
	}

	public int getRole() {
		return role;
	}

	public String getAvatar() {
		return avatar;
	}
	
	public boolean isNeedUpdate() {
		return "".equals(email) || "".equals(nickname);
	}
}
