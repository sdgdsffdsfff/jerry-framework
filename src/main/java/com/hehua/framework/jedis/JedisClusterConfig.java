/**
 * 
 */
package com.hehua.framework.jedis;

import java.util.List;

/**
 * @author zhihua
 *
 */
public class JedisClusterConfig {

    private String name;

    private List<JedisNodeConfig> nodes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JedisNodeConfig> getNodes() {
        return nodes;
    }

    public void setNodes(List<JedisNodeConfig> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "JedisClusterConfig [name=" + name + ", nodes=" + nodes + "]";
    }

}
