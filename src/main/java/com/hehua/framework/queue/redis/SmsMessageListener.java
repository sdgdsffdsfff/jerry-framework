/**
 * 
 */
package com.hehua.framework.queue.redis;

/**
 * @author zhihua
 *
 */
public class SmsMessageListener implements RedisMessageListener {

    @Override
    public void onMessage(String message) {
        System.out.println("send sms: " + message);
    }

}
