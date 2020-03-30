package com.lucky.sso.comm.redis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.lucky.sso.comm.constant.CacheConstant;

public class RedisCacheTemplate {
	 private static final int CACHE_NAMESPACE = CacheConstant.NAMESPACE;
	    private static final String CACHE_GROUP_NAME = CacheConstant.GROUP_NAME;

	    
	    public <T> void put(String key, T t) {

	        put(key, t, -1L);
	    }

	    
	    public <T> void put(String key, T t, long ttl) {
	    }

	    public <T> void putCollection(String key, Collection<T> collection) {
	        putCollection(key, collection, -1L);
	    }

	    
	    public <T> void putCollection(String key, Collection<T> collection, long ttl) {
	    }

	    
	    public <K, V> void putMap(String key, Map<K, V> map) {

	        putMap(key, map, -1L);
	    }

	    
	    public <K, V> void putMap(String key, Map<K, V> map, long ttl) {
	    }

	    
	    public <T> T get(String key, Class<T> clazz) {
	    	return null;
	    }

	    
	    public <T> Collection<T> getCollection(String key, Class<T> itemClazz) {
	    	return null;
	    }

	    
	    @SuppressWarnings("unchecked")
	    public <K, V> Map<K, V> getMap(String key, Class keyType, Class valueType) {
	        Map<K, V> value = new HashMap<>(16);
	        return value;
	    }

	    
	    public void evict(String key) {
	    }
}
