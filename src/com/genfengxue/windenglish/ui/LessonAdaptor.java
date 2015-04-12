package com.genfengxue.windenglish.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.cache.LazyImageViewUriFiller;
import com.genfengxue.windenglish.struct.LessonInfo;
import com.genfengxue.windenglish.struct.LessonInfo.LessonState;

public class LessonAdaptor extends BaseAdapter {

	List<LessonInfo> infos;
	LayoutInflater inflater;
	RelativeLayout layouts[];
	ListView parent;
	
	public LessonAdaptor(ListView parent, List<LessonInfo> infos) {
		this.infos = infos;
		this.parent = parent;
		inflater = LayoutInflater.from(parent.getContext());
		layouts = new RelativeLayout[infos.size()];
	}
	

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LessonInfo info = getItem(position);
		RelativeLayout rl = (RelativeLayout) convertView;
		if (rl == null) {
			rl = (RelativeLayout) inflater.inflate(R.layout.videolistitem, null);
		}
		
		// download image
		ImageView image = (ImageView) rl.findViewById(R.id.ItemImage);
		LazyImageViewUriFiller.fill(rl.getContext(), image, info.getImageUri());

		// put content
		((TextView) rl.findViewById(R.id.ItemTitle)).setText(String.valueOf(info.getLessonId()));
		((TextView) rl.findViewById(R.id.ItemTextChinese)).setText(info.getChTitle());
		((TextView) rl.findViewById(R.id.ItemTextEnglish)).setText(info.getEnTitle());
		Drawable draw = rl.getResources().getDrawable(getIcon(info.getState()));
		((ImageView) rl.findViewById(R.id.ItemState)).setImageDrawable(draw);
		
		ProgressBar bar = (ProgressBar) rl.findViewById(R.id.prograss_bar);
		if (info.getState() == LessonState.DOWNLOADING) {
			bar.setVisibility(View.VISIBLE);
			bar.setProgress(info.getDownloadProgress());
		} else {
			bar.setVisibility(View.INVISIBLE);
		}

		return rl;
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public LessonInfo getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static int getIcon(LessonState state) {
		switch (state) {
		case UNDOWNLOAD:
			return R.drawable.waitingdownload;
		case DOWNLOADED_UNLEARNED:
			return R.drawable.waitingrecord;
		case SUBMITTED:
			return R.drawable.submitted;
		default:
			return R.drawable.waitingdownload;
		}
	}
}
