/**
 * 
 */
package com.hehua.framework.queue.redis;

import java.util.List;

import com.hehua.framework.jedis.PoolableJedis;

/**
 * @author zhihua
 *
 */
public class RedisQueue {

    private PoolableJedis jedis;

    private String name;

    /**
     * @param jedis
     * @param name
     */
    public RedisQueue(PoolableJedis jedis, String name) {
        super();
        this.jedis = jedis;
        this.name = name;
    }

    public void enqueue(String message) {
        jedis.lpush(name, message);
    }

    public List<String> dequeue(int timeout) {
        return jedis.brpop(timeout, name);
    }
}
