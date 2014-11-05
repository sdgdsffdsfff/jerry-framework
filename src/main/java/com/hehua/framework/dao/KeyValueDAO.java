/**
 * 
 */
package com.hehua.framework.dao;

import java.util.Collection;
import java.util.Map;

public interface KeyValueDAO<K, V> {

    public V get(K key);

    public void set(K key, V value);

    public void del(K key);

    public Map<K, V> mget(Collection<K> keys);

    public void mset(Map<K, V> entries);

    public void mdel(Collection<K> keys);

}
