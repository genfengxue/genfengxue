package com.example.windenglish.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.windenglish.utils.Constants;
import com.example.windenglish.utils.FunctionUtils;

/**
 * Fill and Cache images
 * 
 * @author Jack Sun(jacksunwei@gmail.com)
 *
 */
public class ImageViewFillTask extends AsyncTask<String, Void, Bitmap> {

	private ImageView image;

	public ImageViewFillTask(ImageView image) {
		this.image = image;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		String uri = params[0];
		String cachePath = Constants.CACHE_DIR + "/" + FunctionUtils.sha1(uri) + ".jpg";
		
		if (!new File(cachePath).exists()) {
			downloadImage(uri, cachePath);
		}
		
		return BitmapFactory.decodeFile(cachePath);
	}
	
    protected void onPostExecute(Bitmap bitmap) {
    	image.setImageBitmap(bitmap);
    }

	private boolean downloadImage(String uri, String cachePath) {
		try {
			AndroidHttpClient client = AndroidHttpClient.newInstance("");
			HttpGet get = new HttpGet(uri);
			HttpResponse response = client.execute(get);
			File tmpFile = new File(cachePath + "_tmp");

			if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
				return false;
//			ReadableByteChannel rbc = Channels.newChannel(response.getEntity().getContent());
//			FileOutputStream out = new FileOutputStream(tmpFile);
//			long length = response.getEntity().getContentLength();
//			length = (length < 0)? Long.MAX_VALUE : length;
//			out.getChannel().transferFrom(rbc, 0, length);
//			out.close();
			BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
			byte[] buf = new byte[1024];
			int length = 0;
			while ((length = bis.read(buf)) != -1) {
				bos.write(buf, 0, length);
			}
			bos.flush();
			bos.close();
			
			
			client.close();
			tmpFile.renameTo(new File(cachePath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

}
