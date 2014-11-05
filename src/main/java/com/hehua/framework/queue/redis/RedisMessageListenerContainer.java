/**
 * 
 */
package com.hehua.framework.queue.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * @author zhihua
 *
 */
public class RedisMessageListenerContainer implements SignalHandler {

    private static final Log logger = LogFactory.getLog(RedisMessageListenerContainer.class);

    private RedisQueue queue;

    private int consumerCount;

    private ExecutorService consumerExecutor;

    private RedisMessageListener listener;

    private ExecutorService listenerExecutor;

    private List<RedisConsumer> runningConsumers;

    /**
     * @param queue
     * @param consumerCount
     * @param consumerExecutor
     * @param listener
     * @param listenerExecutor
     */
    public RedisMessageListenerContainer(RedisQueue queue, int consumerCount,
            ExecutorService consumerExecutor, RedisMessageListener listener,
            ExecutorService listenerExecutor) {
        super();
        this.queue = queue;
        this.consumerCount = consumerCount;
        this.consumerExecutor = consumerExecutor;
        this.listener = listener;
        this.listenerExecutor = listenerExecutor;
    }

    public synchronized void start() {

        List<RedisConsumer> consumers = new ArrayList<>(consumerCount);
        for (int i = 0; i < consumerCount; i++) {
            RedisConsumer consumer = new RedisConsumer(queue, listener, listenerExecutor);
            consumers.add(consumer);
        }

        this.runningConsumers = consumers;

        for (int i = 0; i < consumerCount; i++) {
            RedisConsumer consumer = runningConsumers.get(i);
            consumerExecutor.execute(consumer);
        }
    }

    public synchronized void stop() {
        stopConsumers();

        consumerExecutor.shutdown();
        try {
            consumerExecutor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            logger.error("ops", e);
        }

        listenerExecutor.shutdown();
        try {
            listenerExecutor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            logger.error("ops", e);
        }

    }

    /**
     * 
     */
    public void stopConsumers() {
        for (int i = 0; i < runningConsumers.size(); i++) {
            RedisConsumer consumer = runningConsumers.get(i);
            try {
                consumer.stop();
            } catch (Throwable e) {
                logger.error("ops", e);
            }
        }

        runningConsumers.clear();
        runningConsumers = null;
    }

    @Override
    public void handle(Signal arg0) {
        try {
            stop();
        } catch (Throwable e) {
            logger.error("error", e);
        } finally {
            System.exit(0);
        }
    }

    public ExecutorService getConsumerExecutor() {
        return consumerExecutor;
    }

    public ExecutorService getListenerExecutor() {
        return listenerExecutor;
    }

}
