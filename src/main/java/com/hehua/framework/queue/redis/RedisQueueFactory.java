/**
 * 
 */
package com.hehua.framework.queue.redis;

import com.hehua.framework.jedis.PoolableJedis;
import com.hehua.framework.jedis.PoolableJedisManager;

/**
 * @author zhihua
 *
 */
public class RedisQueueFactory {

    public static RedisQueue create(String redisCluster, String queueName) {
        PoolableJedis jedis = PoolableJedisManager.getJedis(redisCluster);
        return new RedisQueue(jedis, queueName);
    }
}
