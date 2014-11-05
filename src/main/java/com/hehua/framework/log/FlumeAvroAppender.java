/**
 * 
 */
package com.hehua.framework.log;

import org.apache.flume.FlumeException;
import org.apache.flume.clients.log4jappender.LoadBalancingLog4jAppender;
import org.apache.log4j.spi.LoggingEvent;

import com.hehua.framework.config.ZookeeperConfigManager;

/**
 * @author zhihua
 *
 */
public class FlumeAvroAppender extends LoadBalancingLog4jAppender {

    @Override
    public synchronized void append(LoggingEvent event) throws FlumeException {
        try {
            super.append(event);
        } catch (Exception e) {
            errorHandler.error("error", e, 0, event);
        }
    }

    @Override
    public void activateOptions() throws FlumeException {
        String hosts = ZookeeperConfigManager.getInstance().getString("flume");
        setHosts(hosts);
        super.activateOptions();
    }

}
