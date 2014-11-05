/**
 * 
 */
package com.hehua.framework.queue.redis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.Signal;

import com.google.common.util.concurrent.MoreExecutors;

/**
 * @author zhihua
 *
 */
public class SmsConsumerRunner {

    private static final Log logger = LogFactory.getLog(SmsConsumerRunner.class);

    /**
     * @param args
     */
    public static void main(String[] args) {

        int consumerCount = 2;
        RedisQueue queue = RedisQueueFactory.create("sms", "sms.mq");
        ExecutorService consumerExecutor = Executors.newFixedThreadPool(consumerCount);
        RedisMessageListener listener = new SmsMessageListener();
        ExecutorService listenerExecutor = MoreExecutors.sameThreadExecutor();
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer(
                queue, consumerCount, consumerExecutor, listener, listenerExecutor);

        Signal.handle(new Signal("TERM"), redisMessageListenerContainer);

        try {
            redisMessageListenerContainer.start();
        } catch (Exception e) {
            logger.error("error", e);
            System.exit(0);
        } finally {}
    }

}
