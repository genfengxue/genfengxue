package com.genfengxue.windenglish.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.genfengxue.windenglish.R;

public class WebviewActivity extends Activity {
	private WebView webView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		
		webView = (WebView)findViewById(R.id.webview);
		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl("http://data.genfengxue.com/release_notes?embedded");
	}
}
