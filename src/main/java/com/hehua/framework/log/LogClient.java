/**
 * 
 */
package com.hehua.framework.log;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientConfigurationConstants;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.api.RpcClientFactory.ClientType;
import org.apache.flume.clients.log4jappender.Log4jAvroHeaders;
import org.apache.flume.event.EventBuilder;

import com.hehua.framework.config.ZookeeperConfigManager;

/**
 * @author zhihua
 *
 */
public class LogClient {

    private String hosts;

    private String selector = "RANDOM";

    private String maxBackoff = "10000";

    private String nthreads = "10";

    private long timeout = RpcClientConfigurationConstants.DEFAULT_REQUEST_TIMEOUT_MILLIS;;

    private RpcClient rpcClient;

    /**
     * 
     */
    public LogClient() {
        super();
    }

    /**
     * @param hosts
     * @param selector
     * @param maxBackoff
     * @param timeout
     */
    public LogClient(String hosts, String selector, String maxBackoff, long timeout) {
        super();
        this.hosts = hosts;
        this.selector = selector;
        this.maxBackoff = maxBackoff;
        this.timeout = timeout;
    }

    public void log(String category, Object message) throws FlumeException {

        //        RpcClient rpcClient;
        //        try {
        //            rpcClient = rpcClientPool.borrowObject();
        //        } catch (Exception e1) {
        //            throw new FlumeException(e1);
        //        }
        RpcClient rpcClient = getRpcClient();

        Map<String, String> hdrs = new HashMap<String, String>();
        hdrs.put(Log4jAvroHeaders.LOGGER_NAME.toString(), category);
        hdrs.put(Log4jAvroHeaders.TIMESTAMP.toString(), String.valueOf(System.currentTimeMillis()));

        hdrs.put(Log4jAvroHeaders.MESSAGE_ENCODING.toString(), "UTF8");
        String msg = message.toString();
        Event flumeEvent = EventBuilder.withBody(msg, Charset.forName("UTF8"), hdrs);

        //        long s = System.currentTimeMillis();
        try {
            rpcClient.append(flumeEvent);
        } catch (EventDeliveryException e) {
            throw new FlumeException("Flume append() failed. Exception follows.", e);
        } finally {
            //            try {
            //                rpcClientPool.returnObject(rpcClient);
            //            } catch (Exception e) {
            //                e.printStackTrace();
            //            }
        }
        //        System.out.println("cost " + (System.currentTimeMillis() - s));
    }

    private GenericObjectPool<RpcClient> rpcClientPool = new GenericObjectPool<RpcClient>(
            new PoolableObjectFactory<RpcClient>() {

                @Override
                public RpcClient makeObject() throws Exception {
                    return createRpcClient();
                }

                @Override
                public void destroyObject(RpcClient obj) throws Exception {
                    obj.close();
                }

                @Override
                public boolean validateObject(RpcClient obj) {
                    return obj.isActive();
                }

                @Override
                public void activateObject(RpcClient obj) throws Exception {
                }

                @Override
                public void passivateObject(RpcClient obj) throws Exception {
                }

            }, 30);

    private synchronized RpcClient getRpcClient() {
        if (rpcClient == null) {
            rpcClient = createRpcClient();
        } else if (!rpcClient.isActive()) {
            close();
            rpcClient = createRpcClient();
        }
        return rpcClient;
    }

    private synchronized RpcClient createRpcClient() {
        hosts = ZookeeperConfigManager.getInstance().getString("flume");
        Properties properties = getProperties(hosts, selector, maxBackoff, timeout);

        // 创建ThriftRpcClient
        // Properties properties = getProperties(hosts, nthreads, timeout);
        return RpcClientFactory.getInstance(properties);
    }

    public synchronized void close() throws FlumeException {
        if (rpcClient == null) {
            return;
        }

        try {
            rpcClient.close();
        } catch (FlumeException ex) {} finally {
            rpcClient = null;
        }
    }

    private Properties getProperties(String hosts, String selector, String maxBackoff, long timeout)
            throws FlumeException {

        if (StringUtils.isEmpty(hosts)) {
            throw new FlumeException("hosts must not be null");
        }

        Properties props = new Properties();
        String[] hostsAndPorts = hosts.split("\\s+");
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < hostsAndPorts.length; i++) {
            String hostAndPort = hostsAndPorts[i];
            String name = "h" + i;
            props.setProperty(RpcClientConfigurationConstants.CONFIG_HOSTS_PREFIX + name,
                    hostAndPort);
            names.append(name).append(" ");
        }
        props.put(RpcClientConfigurationConstants.CONFIG_HOSTS, names.toString());
        props.put(RpcClientConfigurationConstants.CONFIG_CLIENT_TYPE,
                ClientType.DEFAULT_LOADBALANCE.toString());
        if (!StringUtils.isEmpty(selector)) {
            props.put(RpcClientConfigurationConstants.CONFIG_HOST_SELECTOR, selector);
        }

        if (!StringUtils.isEmpty(maxBackoff)) {
            long millis = Long.parseLong(maxBackoff.trim());
            if (millis <= 0) {
                throw new FlumeException("Misconfigured max backoff, value must be greater than 0");
            }
            props.put(RpcClientConfigurationConstants.CONFIG_BACKOFF, String.valueOf(true));
            props.put(RpcClientConfigurationConstants.CONFIG_MAX_BACKOFF, maxBackoff);
        }
        props.setProperty(RpcClientConfigurationConstants.CONFIG_CONNECT_TIMEOUT,
                String.valueOf(timeout));
        props.setProperty(RpcClientConfigurationConstants.CONFIG_REQUEST_TIMEOUT,
                String.valueOf(timeout));
        return props;
    }

    private Properties getProperties(String hosts, String nThreads, long timeout) throws FlumeException{
        if (StringUtils.isEmpty(hosts)) {
            throw new FlumeException("hosts must not be null");
        }

        Properties props = new Properties();
        String[] hostsAndPorts = hosts.split("\\s+");
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < hostsAndPorts.length; i++) {
            String hostAndPort = hostsAndPorts[i];
            String name = "h" + i;
            props.setProperty(RpcClientConfigurationConstants.CONFIG_HOSTS_PREFIX + name,
                    hostAndPort);
            names.append(name).append(" ");
        }
        props.put(RpcClientConfigurationConstants.CONFIG_HOSTS, names.toString());
        props.put(RpcClientConfigurationConstants.CONFIG_CLIENT_TYPE, ClientType.THRIFT.toString());
        props.put(RpcClientConfigurationConstants.CONFIG_CONNECTION_POOL_SIZE, nThreads);
        props.setProperty(RpcClientConfigurationConstants.CONFIG_REQUEST_TIMEOUT, String.valueOf(timeout));
        return props;
    }
}
