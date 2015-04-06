package com.genfengxue.windenglish.cache;

import java.util.HashMap;
import java.util.Map;

public class MemoryCache<Key, Value> {

	private Map<Key, Value> cache = new HashMap<Key, Value>();

	public boolean contains(Key key) {
		return cache.containsKey(key);
	}

	public Value get(Key key) {
		return cache.get(key);
	}

	public void put(Key key, Value value) {
		cache.put(key, value);
	}
}
