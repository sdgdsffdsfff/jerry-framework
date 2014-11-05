/**
 * 
 */
package com.hehua.framework.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;

import com.hehua.framework.jedis.PoolableJedis;

/**
 * @author zhihua
 *
 */
public class RedisLock implements Lock {

    private final String lockId;

    private final PoolableJedis jedis;

    private int maxTries = 1000; // 1000*10=10秒

    /**
     * @param lockId
     * @param jedis
     */
    public RedisLock(String lockId, PoolableJedis jedis) {
        super();
        this.lockId = lockId;
        this.jedis = jedis;
    }

    @Override
    public void lock() {
        try {
            lockInterruptibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void lockInterruptibly() throws InterruptedException {
        // TODO 这里实现存在很大问题，先使用这个实现
        int tries = 0;
        while (tries < maxTries) {
            if (tries >= maxTries) {
                throw new InterruptedException("timeout");
            }

            if (tryLock()) {
                return;
            }

            Thread.sleep(TimeUnit.MILLISECONDS.toMillis(10));
            tries++;
        }
    }

    private String getKey() {
        return "lock:" + lockId;
    }

    @Override
    public synchronized boolean tryLock() {
        String key = getKey();
        String value = String.valueOf(Thread.currentThread().getId());
        String result = jedis.set(key, value, "NX", "EX", 10);
        return StringUtils.equalsIgnoreCase("OK", result);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return tryLock();
    }

    @Override
    public void unlock() {
        String key = getKey();
        jedis.del(key);
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

}
