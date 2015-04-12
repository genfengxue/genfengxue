package com.genfengxue.windenglish.cache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.utils.FunctionUtils;

public class ImageLoader {
	
	// make ImageView as weak reference
	private Map<ImageView, String> record = new WeakHashMap<ImageView, String>();
	private Map<String, List<ImageView>> uriQueue = new HashMap<String, List<ImageView>>();
	private MemoryCache<String, Drawable> cache;
	
	public ImageLoader(MemoryCache<String, Drawable> cache) {
		this.cache = cache;
	}

	public void load(Context ctx, ImageView view, String uri) {
		record.put(view, uri);
		if (!uriQueue.containsKey(uri)) {
			uriQueue.put(uri, new ArrayList<ImageView>());
			new ImageDownloadTask(ctx, uri).execute();
		}
		uriQueue.get(uri).add(view);
	}

	private class ImageDownloadTask extends AsyncTask<Void, Void, Drawable> {

		private Context ctx;
		private String uri;
		
		ImageDownloadTask(Context ctx, String uri) {
			this.ctx = ctx;
			this.uri = uri;
		}
		
		@Override
		protected Drawable doInBackground(Void... params) {
			String path = FileCache.getCacheFilePath(uri);
			File cacheFile = new File(path);
			if (!cacheFile.exists()) {
				File tmpFile = new File(path + "_tmp");
				AndroidHttpClient client = AndroidHttpClient.newInstance("Mozilla/5.0");
				HttpGet get = new HttpGet(uri);
				try {
					HttpResponse response = client.execute(get);
					if (200 == response.getStatusLine().getStatusCode()) {
						FunctionUtils.pipeIo(response.getEntity().getContent(), 
								new BufferedOutputStream(new FileOutputStream(tmpFile)));
					}
					
					client.close();
					tmpFile.renameTo(cacheFile);
				} catch (IOException e) {
					e.printStackTrace();
					client.close();
					return ctx.getResources().getDrawable(R.drawable.noimage);
				}
			}
			
			return Drawable.createFromPath(cacheFile.getAbsolutePath());
		}
		
		protected void onPostExecute(Drawable drawable) {
			cache.put(uri, drawable);
			List<ImageView> views = uriQueue.get(uri);
			for (ImageView view : views) {
				String rec = record.get(view);
				if (rec != null && rec.equals(uri)) {
					view.setImageDrawable(drawable);
				}
				record.remove(view);
			}
			uriQueue.remove(uri);
		}

	}
}
