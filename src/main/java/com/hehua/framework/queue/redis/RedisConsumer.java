/**
 * 
 */
package com.hehua.framework.queue.redis;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zhihua
 *
 */
public class RedisConsumer implements Runnable {

    private static final Log logger = LogFactory.getLog(RedisConsumer.class);

    private RedisQueue queue;

    private RedisMessageListener messageListener;

    private ExecutorService listenerExecutor;

    private AtomicBoolean shutdownFlag;

    private static final int TIMEOUT = 10;

    public RedisConsumer(RedisQueue queue, RedisMessageListener messageListener,
            ExecutorService listenerExecutor) {
        super();
        this.queue = queue;
        this.messageListener = messageListener;
        this.listenerExecutor = listenerExecutor;
        this.shutdownFlag = new AtomicBoolean(false);
    }

    private void consumeAndExecuteListener() {
        List<String> pop = consume();
        if (pop == null || pop.isEmpty()) {
            return;
        }

        for (int i = 0; i < pop.size(); i += 2) {
            final String message = pop.get(i + 1);
            if (message == null) {
                continue;
            }

            executeListener(message);
        }
    }

    private List<String> consume() {
        return queue.dequeue(TIMEOUT);
    }

    private void executeListener(final String message) {
        listenerExecutor.execute(new Runnable() {

            @Override
            public void run() {
                messageListener.onMessage(message);
            }
        });
    }

    @Override
    public void run() {
        while (!shutdownFlag.get()) {
            try {
                consumeAndExecuteListener();
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
    }

    public void stop() {
        shutdownFlag.set(true);
    }
}
