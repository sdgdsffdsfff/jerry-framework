/**
 * 
 */
package com.hehua.framework.queue.redis;

/**
 * @author zhihua
 *
 */
public interface RedisMessageListener {

    public void onMessage(String message);
}
