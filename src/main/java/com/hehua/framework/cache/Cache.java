/**
 * 
 */
package com.hehua.framework.cache;

import java.util.Collection;
import java.util.Map;

import com.hehua.framework.dao.KeyValueDAO;

/**
 * @author zhihua
 *
 */
public interface Cache<K, V> extends KeyValueDAO<K, V> {

    @Override
    public V get(K key);

    @Override
    public Map<K, V> mget(Collection<K> keys);

    @Override
    public void set(K key, V value);

    @Override
    public void mset(Map<K, V> entries);

    @Override
    public void del(K key);

    @Override
    public void mdel(Collection<K> keys);

}
