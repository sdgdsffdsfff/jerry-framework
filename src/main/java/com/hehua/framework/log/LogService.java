/**
 * 
 */
package com.hehua.framework.log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.flume.FlumeException;

/**
 * @author zhihua
 *
 */
public class LogService {

    private ExecutorService executor = Executors.newFixedThreadPool(16);

    private static final LogService INSTANCE = new LogService();

    private static final LogClient logClient = LogClientFactory.createLogClient();

    private static final Log logger = LogFactory.getLog(LogService.class);

    private static final Log flumeEventFailoverLogger = LogFactory
            .getLog("flumeEventFailoverLogger");

    public static LogService getInstance() {
        return INSTANCE;
    }

    public void log(Object message) {
        log(LogCategory.DEFAULT, message);
    }

    public void info(Object message) {
        log(LogCategory.DEFAULT, message);
    }

    public void log(final String category, final Object message) {

        executor.execute(new Runnable() {

            @Override
            public void run() {
                // TODO 如果队列太长了，则进行failover
                logInternal(category, message);

            }
        });
    }

    public void logInternal(String category, Object message) {

        try {
            logClient.log(category, message);
        } catch (FlumeException e) {
            failover(category, message, e);
        }
    }

    /**
     * @param category
     * @param message
     * @param e
     */
    public void failover(String category, Object message, Exception e) {
        String msg = category + " ## " + message.toString();

        flumeEventFailoverLogger.info(msg);

        if (e != null) {
            logger.warn("log failover: msg = " + msg, e);
        } else {
            logger.warn("log failover: msg = " + msg);
        }
    }

    public static void main(String[] args) {

    }

}
