/**
 * 
 */
package com.hehua.framework.jedis;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.alibaba.fastjson.JSON;
import com.hehua.commons.resources.CompositeResourceLoader;
import com.hehua.commons.resources.SystemPropertyResourceLoader;

/**
 * @author zhihua
 *
 */
public class JedisConfigLoader {

    private static final Log logger = LogFactory.getLog(JedisConfigLoader.class);

    private static final String CONFIG_FILE_SYS = "jedis.configuration";

    private static final String CONFIG_FILE = "config/jedis.conf";

    private ResourceLoader resourceLoader = new CompositeResourceLoader(new ResourceLoader[] {
            new SystemPropertyResourceLoader(CONFIG_FILE_SYS), new DefaultResourceLoader() });

    public JedisConfigLoader() {
        super();
    }

    public List<JedisClusterConfig> loadConfig() {
        String configStr = loadConfigAsString();
        return JSON.parseArray(configStr, JedisClusterConfig.class);
    };

    private String loadConfigAsString() {
        Resource resource = resourceLoader.getResource(CONFIG_FILE);
        String content = null;
        try (InputStream is = resource.getInputStream()) {
            content = IOUtils.toString(is);
        } catch (IOException e) {
            logger.error("ops!", e);
            throw new RuntimeException("error happened when ", e);
        }
        return content;
    }

}
