package com.genfengxue.windenglish.cache;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.genfengxue.windenglish.R;

public class LazyImageViewUriFiller {

	private static MemoryCache<String, Drawable> cache;

	private static ImageLoader loader;
	
	static {
		cache = new MemoryCache<String, Drawable>();
		loader = new ImageLoader(cache);
	}
	
	public static void fill(Context ctx, ImageView view, String uri) {
		if (cache.contains(uri)) {
			view.setImageDrawable(cache.get(uri));
			return;
		}
		view.setImageResource(R.drawable.ic_launcher);
		loader.load(ctx, view, uri);
	}
}
