package com.example.windenglish.cache;

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

import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.windenglish.R;
import com.example.windenglish.utils.Constants;
import com.example.windenglish.utils.FunctionUtils;

public class ImageLoader {

	
	// make ImageView as weak reference
	private Map<ImageView, String> record = new WeakHashMap<ImageView, String>();
	private Map<String, List<ImageView>> uriQueue = new HashMap<String, List<ImageView>>();
	private MemoryCache<String, Drawable> cache;
	
	public ImageLoader(MemoryCache<String, Drawable> cache) {
		this.cache = cache;
	}

	public void load(ImageView view, String uri) {
		record.put(view, uri);
		if (!uriQueue.containsKey(uri)) {
			uriQueue.put(uri, new ArrayList<ImageView>());
			new ImageDownloadTask(uri).execute();
		}
		uriQueue.get(uri).add(view);
	}

	private class ImageDownloadTask extends AsyncTask<Void, Void, Drawable> {

		private String uri;
		
		ImageDownloadTask(String uri) {
			this.uri = uri;
		}
		
		@Override
		protected Drawable doInBackground(Void... params) {
			String filename = FunctionUtils.sha1(uri);
			File cacheFile = new File(Constants.CACHE_DIR, filename);
			if (!cacheFile.exists()) {
				File tmpFile = new File(Constants.CACHE_DIR, filename + "_tmp");
				AndroidHttpClient client = AndroidHttpClient.newInstance("Mozilla/5.0");
				HttpGet get = new HttpGet(uri);
				try {
					HttpResponse response = client.execute(get);
					if (200 == response.getStatusLine().getStatusCode()) {
						FunctionUtils.pipeIo(response.getEntity().getContent(), 
								new BufferedOutputStream(new FileOutputStream(tmpFile)));
					}
					tmpFile.renameTo(cacheFile);
				} catch (IOException e) {
					e.printStackTrace();
					return Constants.MAIN_CONTEXT.getResources().getDrawable(R.drawable.noimage);
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
