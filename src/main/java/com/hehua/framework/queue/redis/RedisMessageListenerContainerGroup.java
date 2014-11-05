/**
 * 
 */
package com.hehua.framework.queue.redis;

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
public class RedisMessageListenerContainerGroup implements SignalHandler {

    private static final Log logger = LogFactory.getLog(RedisMessageListenerContainerGroup.class);

    private List<RedisMessageListenerContainer> containers;

    private boolean start;

    /**
     * @param containers
     */
    public RedisMessageListenerContainerGroup(List<RedisMessageListenerContainer> containers) {
        super();
        this.containers = containers;
    }

    public synchronized void start() {
        if (start) {
            throw new RuntimeException("already started");
        }

        for (RedisMessageListenerContainer container : containers) {
            container.start();
        }
    }

    public synchronized void stop() {

        if (!start) {
            throw new RuntimeException("not start");
        }

        logger.info("stop consumers");
        for (RedisMessageListenerContainer container : containers) {
            try {
                container.stopConsumers();
            } catch (Exception e) {
                logger.error("error", e);
            }
        }

        logger.info("shutdown executors");
        for (RedisMessageListenerContainer container : containers) {
            try {
                ExecutorService consumerExecutor = container.getConsumerExecutor();
                if (!consumerExecutor.isShutdown()) {
                    consumerExecutor.shutdown();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }

            try {
                ExecutorService listenerExecutor = container.getListenerExecutor();
                if (!listenerExecutor.isShutdown()) {
                    listenerExecutor.shutdown();
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }

        logger.info("terminate executors");
        for (RedisMessageListenerContainer container : containers) {
            try {
                ExecutorService consumerExecutor = container.getConsumerExecutor();
                if (!consumerExecutor.isTerminated()) {
                    consumerExecutor.awaitTermination(1, TimeUnit.DAYS);
                }
            } catch (Exception e) {
                logger.error("error", e);
            }

            try {
                ExecutorService listenerExecutor = container.getListenerExecutor();
                if (!listenerExecutor.isTerminated()) {
                    listenerExecutor.awaitTermination(1, TimeUnit.DAYS);
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
        logger.info("stop success");
    }

    @Override
    public void handle(Signal arg0) {
        try {
            logger.info("handle signal " + arg0.getName() + "#" + arg0.getNumber());
            stop();
        } catch (Throwable e) {
            logger.error("error", e);
        } finally {
            logger.info("exit");
            System.exit(0);
        }
    }

}
