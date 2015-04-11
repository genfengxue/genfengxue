package com.genfengxue.windenglish.struct;

public class UserProfile {
	public static final int USER_ROLE_STUDENT = 1;
	public static final int USER_ROLE_TEACHER = 2;
	public static final int USER_ROLE_ADMIN = 3;

	private int userNo;
	private int role;
	private String nickName;
	private String avatar;
	private String email;

	public UserProfile(int userNo, int role, String nickName, String avatar,
			String email) {
		this.userNo = userNo;
		this.role = role;
		this.nickName = nickName;
		this.avatar = avatar;
		this.email = email;
	}

	public String getName() {
		return nickName;
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

	public String getNickName() {
		return nickName;
	}

	public String getAvatar() {
		return avatar;
	}
}
