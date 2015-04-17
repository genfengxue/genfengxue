package com.genfengxue.windenglish.struct;

import java.io.File;

import com.genfengxue.windenglish.utils.UriUtils;

public class LessonInfo {

	public static enum LessonState {
		UNDOWNLOAD, DOWNLOADING, DOWNLOADED
	}
	
	/**
	 * Learn State <br>
	 * the number should increase along with the learn progress
	 */
	public static final int NOT_LEARNED = 0;
	public static final int WATCH_1_VIDEO = 1;
	public static final int WATCH_2_VIDEO = 2;
	public static final int WATCH_3_VIDEO = 3;
	public static final int RECORDED = 4;
	public static final int SUBMITTED = 5;
	
	private int lessonNo;
	private int courseNo;
	private int learnState;
	private String chTitle;
	private String enTitle;
	private String imageUri;

	private String videoUri;
	private String videoPath;
	private LessonState downloadState;
	private int downloadProgress;

	public LessonInfo(int lessonNo, int courseNo, int learnState, String chTitle,
			String enTitle, String imageUri) {
		this.lessonNo = lessonNo;
		this.courseNo = courseNo;
		this.learnState = learnState;
		this.chTitle = chTitle;
		this.enTitle = enTitle;
		this.imageUri = imageUri;
		this.downloadProgress = 0;
		updateState();
	}

	public static String preferenceKey(int courseId, int lessonId) {
		return courseId + "-" + lessonId;
	}
	
	public int getLessonNo() {
		return lessonNo;
	}

	public int getCourseNo() {
		return courseNo;
	}

	public String getVideoUri(int part) {
		if (videoUri == null) {
			videoUri = UriUtils.getLessonVideoUri(courseNo, lessonNo, part);
		}

		return videoUri;
	}

	public String getVideoPath(int part) {
		if (videoPath == null) {
			videoPath = UriUtils.getLessonVideoPath(courseNo, lessonNo, part);
		}

		return videoPath;
	}
	
	public int getLearnState() {
		return learnState;
	}

	public LessonState getDownloadState() {
		return downloadState;
	}
	
	public void setDownloadState() {
		this.downloadState = LessonState.DOWNLOADING;
	}
	
	public int getDownloadProgress() {
		return downloadProgress;
	}

	public void setDownloadProgress(int downloadPercent) {
		this.downloadProgress = downloadPercent;
	}

	// TODO add state detection for unsubmitted
	public void updateState() {
		String part4 = getVideoPath(4);
		if (!(new File(part4).exists())) {
			downloadState = LessonState.UNDOWNLOAD;
		} else {
			downloadState = LessonState.DOWNLOADED;
		}
	}

	public String getImageUri() {
		return imageUri;
	}

	public String getChTitle() {
		return chTitle;
	}

	public String getEnTitle() {
		return enTitle;
	}

	public String toString() {
		return "Course " + courseNo + ", Lesson " + lessonNo;
	}
}
