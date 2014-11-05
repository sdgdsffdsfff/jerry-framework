/**
 * 
 */
package com.hehua.framework.jedis;

/**
 * @author zhihua
 *
 */
public class JedisNodeConfig {

    private String host;

    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "JedisNodeConfig [host=" + host + ", port=" + port + "]";
    }

}
