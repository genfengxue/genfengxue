package com.example.windenglish.ui;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.windenglish.R;
import com.example.windenglish.cache.ImageViewFillTask;
import com.example.windenglish.struct.LessonInfo;
import com.example.windenglish.struct.LessonInfo.LessonState;

public class LessonAdaptor extends ArrayAdapter<LessonInfo> {

	public LessonAdaptor(Context context, int resource, int textViewResourceId,
			List<LessonInfo> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout rl = (RelativeLayout) super.getView(position,
				convertView, parent);
		LessonInfo info = getItem(position);

		// download image
		ImageView image = (ImageView) rl.findViewById(R.id.ItemImage);
		new ImageViewFillTask(image).execute(info.getImageUri());

		((TextView) rl.findViewById(R.id.ItemTitle)).setText(String.valueOf(info.getLessonId()));
		((TextView) rl.findViewById(R.id.ItemTextChinese)).setText(info.getChTitle());
		((TextView) rl.findViewById(R.id.ItemTextEnglish)).setText(info.getEnTitle());
		Drawable draw = rl.getResources().getDrawable(getIcon(info.getState()));
		((ImageView) rl.findViewById(R.id.ItemState)).setImageDrawable(draw);

		return rl;
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
