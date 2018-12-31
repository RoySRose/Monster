package org.monster.micro.temporaryutil;

import java.util.HashMap;
import java.util.Map;

public class HashMapBuilder<K, V> {
	private Map<K, V> hashMap = new HashMap<>();
	
	public HashMapBuilder<K, V> put(K key, V value) {
		hashMap.put(key, value);
		return this;
	}
	
	public Map<K, V> build() {
		return hashMap;
	}
}
