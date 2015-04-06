package com.genfengxue.windenglish.struct;

import java.io.File;

import com.genfengxue.windenglish.utils.UriUtils;

public class LessonInfo {

	public static enum LessonState {
		UNDOWNLOAD, DOWNLOADED_UNLEARNED, SUBMITTED
	}

	private int lessonId;
	private int courseId;
	private String chTitle;
	private String enTitle;
	private String imageUri;

	private String videoUri;
	private String videoPath;
	private LessonState state;

	public LessonInfo(int lessonId, int courseId, String chTitle,
			String enTitle, String imageUri) {
		this.lessonId = lessonId;
		this.courseId = courseId;
		this.chTitle = chTitle;
		this.enTitle = enTitle;
		this.imageUri = imageUri;
		init();
	}

	public int getLessonId() {
		return lessonId;
	}

	public void setLessonId(int id) {
		this.lessonId = id;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public String getVideoUri(int part) {
		if (videoUri == null) {
			videoUri = UriUtils.getLessonVideoUri(courseId, lessonId, part);
		}

		return videoUri;
	}

	public String getVideoPath(int part) {
		if (videoPath == null) {
			videoPath = UriUtils.getLessonVideoPath(courseId, lessonId, part);
		}

		return videoPath;
	}

	public LessonState getState() {
		return state;
	}

	public String getImageUri() {
		return imageUri;
	}

	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}

	public String getChTitle() {
		return chTitle;
	}

	public void setChTitle(String chDescription) {
		this.chTitle = chDescription;
	}

	public String getEnTitle() {
		return enTitle;
	}

	public void setEnTitle(String enDescription) {
		this.enTitle = enDescription;
	}

	public String toString() {
		return "Course " + courseId + ", Lesson " + lessonId;
	}

	private void init() {
		String part1 = getVideoPath(1);
		if (!(new File(part1).exists())) {
			state = LessonState.UNDOWNLOAD;
		} else {
			state = LessonState.DOWNLOADED_UNLEARNED;
		}
	}
}
