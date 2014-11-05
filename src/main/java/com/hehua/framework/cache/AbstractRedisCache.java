/**
 * 
 */
package com.hehua.framework.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.hehua.framework.codec.Codec;
import com.hehua.framework.jedis.PoolableJedis;

/**
 * @author zhihua
 *
 */
public abstract class AbstractRedisCache<K, V> implements Cache<K, V>, Codec<V, String> {

    protected final PoolableJedis jedis;

    /**
     * @param jedis
     * @param
     */
    public AbstractRedisCache(PoolableJedis jedis) {
        this.jedis = jedis;
    }

    public abstract String buildKey(K key);

    public PoolableJedis getRedis() {
        return this.jedis;
    }

    @Override
    public V get(K key) {
        String string = jedis.get(buildKey(key));
        return decode(string);
    }

    @Override
    public Map<K, V> mget(Collection<K> keys) {
        Map<String, K> keyMap = new HashMap<>();
        for (K key : keys) {
            keyMap.put(buildKey(key), key);
        }

        Map<String, String> stringMap = jedis.mget(keyMap.keySet());
        Map<K, V> result = new HashMap<>(stringMap.size());
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            result.put(keyMap.get(entry.getKey()), decode(entry.getValue()));
        }
        return result;
    }

    @Override
    public void set(K key, V value) {
        String stringValue = encode(value);
        int expire = getExpire();
        if (expire > 0) {
            jedis.setex(buildKey(key), expire, stringValue);
        } else {
            jedis.set(buildKey(key), stringValue);
        }
    }

    @Override
    public void mset(Map<K, V> entries) {
        Map<String, String> result = new HashMap<>(entries.size());
        for (Map.Entry<K, V> entry : entries.entrySet()) {
            result.put(buildKey(entry.getKey()), encode(entry.getValue()));
        }
        int expire = getExpire();
        if (expire > 0) {
            jedis.mset(result, expire);
        } else {
            jedis.mset(result);
        }
    }

    @Override
    public void del(K key) {
        jedis.del(buildKey(key));
    }

    @Override
    public void mdel(Collection<K> keys) {
        jedis.mdel(buildKeys(keys));
    }

    private Collection<String> buildKeys(Collection<K> keys) {
        List<String> result = new ArrayList<>(keys.size());
        for (K key : keys) {
            result.add(buildKey(key));
        }
        return result;
    }

    public int getExpire() {
        return (int) TimeUnit.DAYS.toSeconds(1);
    }

}
