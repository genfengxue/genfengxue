package com.genfengxue.windenglish.struct;

import org.json.JSONObject;

public class CourseInfo {
	private int courseNo;
	private String imageUrl, titleCh, titleEn, description;

	public CourseInfo(int courseNo, String titleCh, String titleEn,
			String description, String imageUrl) {
		this.courseNo = courseNo;
		this.titleCh = titleCh;
		this.titleEn = titleEn;
		this.description = description;
		this.imageUrl = imageUrl;
	}
	
	public static CourseInfo load(JSONObject obj) {
		if (obj == null) {
			return null;
		}
		int courseNo = obj.optInt("courseNo");
		String titleCh = obj.optString("chineseTitle");
		String titleEn = obj.optString("englishTitle");
		String imageUrl = obj.optString("imageUrl");
		String desc = obj.optString("description");
		return new CourseInfo(courseNo, titleCh, titleEn, desc, imageUrl);
	}

	public int getCourseNo() {
		return courseNo;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getTitleCh() {
		return titleCh;
	}

	public String getTitleEn() {
		return titleEn;
	}

	public String getDescription() {
		return description;
	}
}
