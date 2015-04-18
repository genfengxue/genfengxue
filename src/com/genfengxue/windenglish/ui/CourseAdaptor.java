package com.genfengxue.windenglish.ui;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.cache.LazyImageViewUriFiller;
import com.genfengxue.windenglish.struct.CourseInfo;

public class CourseAdaptor extends BaseAdapter {
	private List<CourseInfo> infos;
	LayoutInflater inflater;
	
	public CourseAdaptor(Context ctx, List<CourseInfo> infos) {
		this.infos = infos;
		inflater = LayoutInflater.from(ctx);
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public CourseInfo getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CourseInfo info = getItem(position);
		RelativeLayout rl = (RelativeLayout) convertView;
		if (rl == null) {
			rl = (RelativeLayout) inflater.inflate(R.layout.courselistitem, null);
		}
		
		ImageView image = (ImageView) rl.findViewById(R.id.course_image);
		LazyImageViewUriFiller.fill(rl.getContext(), image, info.getImageUrl());
		
		((TextView) rl.findViewById(R.id.title_ch)).setText(info.getTitleCh());
		((TextView) rl.findViewById(R.id.title_en)).setText(info.getTitleEn());
		((TextView) rl.findViewById(R.id.item_description)).setText(info.getDescription());
		
		return rl;
	}
	
}
