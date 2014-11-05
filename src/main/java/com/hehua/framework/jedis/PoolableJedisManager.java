/**
 * 
 */
package com.hehua.framework.jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhihua
 *
 */
public class PoolableJedisManager {

    private static final PoolableJedisFactory poolableJedisFactory = PoolableJedisFactory
            .getInstance();

    private static final Map<String, PoolableJedis> redisMap = new HashMap<>();

    public static PoolableJedis getJedis(String name) {
        return getPoolableJedis(name);
    }

    public static PoolableJedis getPoolableJedis(String name) {

        PoolableJedis jedis = redisMap.get(name);
        if (jedis != null) {
            return jedis;
        }

        synchronized (redisMap) {
            jedis = redisMap.get(name);
            if (jedis != null) {
                return jedis;
            }

            jedis = poolableJedisFactory.create(name);
            redisMap.put(name, jedis);
            return jedis;
        }
    }

    public static PoolableJedis getDefaultCacheJedis() {
        return getJedis("cache1");
    }
}
