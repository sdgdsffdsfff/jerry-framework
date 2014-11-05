/**
 * 
 */
package com.hehua.framework.cache;

import java.util.LinkedHashMap;

/**
 * 
 * @author zhouzhihua <zhihua@afanda.com>
 * @version 1.0 create at Oct 24, 2012 7:04:58 PM
 */
public class LRUCache<K, V> {

    private final InnerMap<K, V> innerCache;

    public LRUCache(int maxEntries) {
        this.innerCache = new InnerMap<K, V>(maxEntries);
    }

    public synchronized V get(K key) {
        return innerCache.get(key);
    }

    public synchronized V set(K key, V value) {
        return innerCache.put(key, value);
    }

    public synchronized V delete(K key) {
        return innerCache.remove(key);
    }

    public synchronized void clear() {
        innerCache.clear();
    }

    private static class InnerMap<K, V> extends LinkedHashMap<K, V> {

        private static final long serialVersionUID = 1L;

        private final int maxEntries;

        /**
         * @param maxEntries
         */
        public InnerMap(int maxEntries) {
            super();
            this.maxEntries = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
            boolean removeEldestEntry = super.size() > maxEntries;
            return removeEldestEntry;
        }
    }
}
