/**
 * 
 */
package com.hehua.framework.jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhihua
 *
 */
public class JedisConfigManager {

    private final Map<String, JedisClusterConfig> configByName = new HashMap<>();

    public JedisConfigManager() {
        super();
        init();
    }

    private void init() {

        ZookeeperJedisConfigLoader jedisConfigLoader = new ZookeeperJedisConfigLoader();
        List<JedisClusterConfig> configList = jedisConfigLoader.loadConfig();
        System.out.println(configList);

        for (JedisClusterConfig config : configList) {
            this.configByName.put(config.getName(), config);
        }
    }

    public JedisClusterConfig getConfig(String name) {
        return this.configByName.get(name);
    }

}
