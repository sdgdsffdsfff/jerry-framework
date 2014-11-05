/**
 * 
 */
package com.hehua.framework.queue.redis;

/**
 * @author zhihua
 *
 */
public class SmsProductor {

    /**
     * @param args
     */
    public static void main(String[] args) {

        int consumerCount = 1;
        RedisQueue queue = RedisQueueFactory.create("sms", "sms.mq");

        for (int i = 0; i < 1000; i++) {
            queue.enqueue("message" + i);
        }

    }

}
