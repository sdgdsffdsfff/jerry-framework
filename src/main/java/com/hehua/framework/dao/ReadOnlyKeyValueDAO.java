/**
 * 
 */
package com.hehua.framework.dao;

import java.util.Collection;
import java.util.Map;

/**
 * @author zhihua
 *
 */
public abstract class ReadOnlyKeyValueDAO<K, V> implements KeyValueDAO<K, V> {

    @Override
    public void set(K key, V value) {
    }

    @Override
    public void del(K key) {
    }

    @Override
    public void mset(Map<K, V> entries) {
    }

    @Override
    public void mdel(Collection<K> keys) {
    }

}
