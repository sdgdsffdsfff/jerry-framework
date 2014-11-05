/**
 * 
 */
package com.hehua.framework.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

/**
 * 
 * TODO add cache stat
 * 
 * @author zhihua
 *
 */
public class MultilayerKeyValueDAO<K, V> implements KeyValueDAO<K, V> {

    private final ArrayList<KeyValueDAO<K, V>> daos;

    /**
     * @param daos
     */
    public MultilayerKeyValueDAO(ArrayList<KeyValueDAO<K, V>> daos) {
        super();
        this.daos = daos;
    }

    @Override
    public V get(K key) {
        for (int i = 0; i < daos.size(); i++) {
            KeyValueDAO<K, V> dao = daos.get(i);
            V value = dao.get(key);
            if (value != null) {
                for (int j = i - 1; j >= 0; j--) {
                    daos.get(j).set(key, value);
                }
                return value;
            }
        }
        return null;
    }

    @Override
    public void set(K key, V value) {
        for (int i = daos.size() - 1; i >= 0; i--) {
            KeyValueDAO<K, V> dao = daos.get(i);
            dao.set(key, value);
        }
    }

    @Override
    public void del(K key) {
        for (int i = daos.size() - 1; i >= 0; i--) {
            KeyValueDAO<K, V> dao = daos.get(i);
            dao.del(key);
        }
    }

    @Override
    public Map<K, V> mget(Collection<K> keys) {

        // 处理空查询
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptyMap();
        }

        // 逐层访问
        ArrayList<Map<K, V>> results = new ArrayList<>(daos.size());
        Set<K> unhits = new HashSet<>(keys);
        for (int i = 0; i < daos.size(); i++) {

            KeyValueDAO<K, V> dao = daos.get(i);
            Map<K, V> values = dao.mget(unhits);
            results.add(values);
            unhits = new HashSet<>(unhits.size() - values.size());
            for (K key : keys) {
                if (!values.containsKey(key)) {
                    unhits.add(key);
                }
            }

            if (unhits.isEmpty()) {
                break;
            }
        }

        // 回写
        for (int i = results.size() - 2; i >= 0; i--) {
            // 这层丢失的
            Map<K, V> misses = new HashMap<>();
            for (int j = i + 1; j < results.size(); j++) {
                misses.putAll(results.get(j));
            }
            KeyValueDAO<K, V> dao = daos.get(i);
            dao.mset(misses);
        }

        // 合并结果
        Map<K, V> result = new HashMap<>(keys.size());
        for (int i = 0; i < results.size(); i++) {
            result.putAll(results.get(i));
        }
        return result;
    }

    @Override
    public void mset(Map<K, V> entries) {

        if (MapUtils.isEmpty(entries)) {
            return;
        }

        for (int i = daos.size() - 1; i >= 0; i--) {
            KeyValueDAO<K, V> dao = daos.get(i);
            dao.mset(entries);
        }
    }

    @Override
    public void mdel(Collection<K> keys) {

        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

        for (int i = daos.size() - 1; i >= 0; i--) {
            KeyValueDAO<K, V> dao = daos.get(i);
            dao.mdel(keys);
        }
    }

}
