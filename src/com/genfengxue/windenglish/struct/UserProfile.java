package com.genfengxue.windenglish.struct;

import java.util.Properties;

public class UserProfile {
	public static final int USER_ROLE_STUDENT = 1;
	public static final int USER_ROLE_TEACHER = 2;
	public static final int USER_ROLE_ADMIN = 3;

	private int userNo;
	private int role;
	private String nickname;
	private String avatar;
	private String email;

	public UserProfile(int userNo, int role, String nickName, String avatar,
			String email) {
		this.userNo = userNo;
		this.role = role;
		this.nickname = nickName;
		this.avatar = avatar;
		this.email = email;
	}
	
	public static UserProfile load(Properties props) {
		int userNo = Integer.valueOf(props.getProperty("userNo", ""));
		int role = Integer.valueOf(props.getProperty("role"));
		String nickName = props.getProperty("nickname", "User: " + userNo);
		String avatar = props.getProperty("avatar", "");
		String email = props.getProperty("email", "");
		return new UserProfile(userNo, role, nickName, avatar, email);
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
}
