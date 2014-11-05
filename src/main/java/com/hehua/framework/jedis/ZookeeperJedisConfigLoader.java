/**
 * 
 */
package com.hehua.framework.jedis;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.hehua.framework.config.ZookeeperConfigManager;

/**
 * @author zhihua
 *
 */
public class ZookeeperJedisConfigLoader {

    private static final Log logger = LogFactory.getLog(ZookeeperJedisConfigLoader.class);

    private static final String KEY_JEDIS = "redis";

    public ZookeeperJedisConfigLoader() {
        super();
    }

    public List<JedisClusterConfig> loadConfig() {
        String configStr = loadConfigAsString();
        return JSON.parseArray(configStr, JedisClusterConfig.class);
    };

    private String loadConfigAsString() {
        return ZookeeperConfigManager.getInstance().getString(KEY_JEDIS);
    }

}
