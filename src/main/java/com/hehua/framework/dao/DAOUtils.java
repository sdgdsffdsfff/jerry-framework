/**
 * 
 */
package com.hehua.framework.dao;

import com.google.common.collect.Lists;

/**
 * @author zhihua
 *
 */
public class DAOUtils {

    @SafeVarargs
    public static <K, V> MultilayerKeyValueDAO<K, V> multiDAO(KeyValueDAO<K, V>... daos) {
        return new MultilayerKeyValueDAO<K, V>(Lists.newArrayList(daos));
    }
}
