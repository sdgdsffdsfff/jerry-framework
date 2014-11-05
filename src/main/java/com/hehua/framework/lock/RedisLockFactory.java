/**
 * 
 */
package com.hehua.framework.lock;

import com.hehua.framework.jedis.PoolableJedis;
import com.hehua.framework.jedis.PoolableJedisManager;

/**
 * @author zhihua
 *
 */
public class RedisLockFactory {

    public static RedisLock newLock(String lockId) {
        PoolableJedis jedis = PoolableJedisManager.getDefaultCacheJedis();
        return new RedisLock(lockId, jedis);
    }

}
