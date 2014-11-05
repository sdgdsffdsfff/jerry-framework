/**
 * 
 */
package com.hehua.framework.config.database;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hehua.framework.localcache.AbstractLocalCache;

/**
 * @author zhihua
 *
 */
@Component
public class DatabaseConfigLocalCache extends AbstractLocalCache<Map<String, String>> {

    @Autowired
    private DatabaseConfigDAO configDAO;

    @Override
    public String key() {
        return "config";
    }

    @Override
    public Map<String, String> load() {
        List<DatabaseConfig> configs = configDAO.getAll();
        Map<String, String> configsMap = new HashMap<>(configs.size());
        for (DatabaseConfig config : configs) {
            configsMap.put(config.getName(), config.getValue());
        }
        return Collections.unmodifiableMap(configsMap);
    }

}
