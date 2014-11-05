/**
 * 
 */
package com.hehua.framework.jms.transport;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.TransportFactory;
import org.apache.activemq.transport.TransportServer;
import org.apache.activemq.transport.failover.FailoverTransportFactory;

import com.hehua.framework.config.ZookeeperConfigManager;

public class ZookeeperConfigTransportFactory extends TransportFactory {

    private static final String CONFIG_KEY = "jms";

    private TransportFactory transportFactory = new FailoverTransportFactory();

    @Override
    public Transport doConnect(URI location) throws Exception {
        return transportFactory.doConnect(getConfigURI());
    }

    private URI getConfigURI() throws URISyntaxException {
        String sourceURL = ZookeeperConfigManager.getInstance().getString(CONFIG_KEY);
        URI brokerURL = new URI(sourceURL);
        return brokerURL;
    }

    @Override
    public Transport doCompositeConnect(URI location) throws Exception {
        return transportFactory.doCompositeConnect(getConfigURI());
    }

    @Override
    public TransportServer doBind(URI paramURI) throws IOException {
        try {
            return transportFactory.doBind(getConfigURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
