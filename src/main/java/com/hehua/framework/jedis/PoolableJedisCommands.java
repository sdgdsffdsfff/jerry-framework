package com.hehua.framework.jedis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class PoolableJedisCommands implements InvocationHandler {

    private ShardedJedisPool jedis;

    /**
     * @param jedis
     */
    PoolableJedisCommands(ShardedJedisPool jedis) {
        this.jedis = jedis;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ShardedJedis resource = null;
        boolean success = false;
        try {
            resource = jedis.getResource();
            Object invoke = method.invoke(resource, args);
            success = true;
            return invoke;
        } finally {
            if (success) {
                if (resource != null) {
                    jedis.returnResourceObject(resource);
                }
            } else {
                if (resource != null) {
                    jedis.returnBrokenResource(resource);
                }
            }
        }
    }
}
