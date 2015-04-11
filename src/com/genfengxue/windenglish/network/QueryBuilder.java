package com.genfengxue.windenglish.network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {

	private static final String CHARSET = "UTF-8";

	private List<String> queries = new ArrayList<String>();
	
	public void addIntQuery(String key, int value) {
		addStringQuery(key, String.valueOf(value));
	}
	
	public void addStringQuery(String key, String value) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(URLEncoder.encode(key, CHARSET));
			sb.append("=");
			sb.append(URLEncoder.encode(value, CHARSET));
			queries.add(sb.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public String getQuery() {
		StringBuilder sb = new StringBuilder();
		for (String q : queries) {
			sb.append(q).append("&");
		}
		
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
