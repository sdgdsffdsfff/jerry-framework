/**
 * 
 */
package com.hehua.framework.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author zhihua
 *
 */
public class KeyValueDAOUtils {

    public <K, V> V getDataFromHierarchicalStorages(ArrayList<KeyValueDAO<K, V>> daos, K key) {
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

    public <K, V> Map<K, V> getDatasFromHierarchicalStorages(ArrayList<KeyValueDAO<K, V>> daos,
            Collection<K> keys) {
        Map<K, V> result = new HashMap<>();
        for (int i = 0; i < daos.size(); i++) {
            KeyValueDAO<K, V> dao = daos.get(i);
            Map<K, V> values = dao.mget(keys);
            result.putAll(values);

            Set<K> unhits = new HashSet<>(keys.size() - result.size());
            for (K key : keys) {
                if (!result.containsKey(key)) {
                    unhits.add(key);
                }
            }
        }
        return null;
    }
}
