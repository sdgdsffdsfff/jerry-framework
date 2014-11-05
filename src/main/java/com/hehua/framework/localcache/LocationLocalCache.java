/**
 * 
 */
package com.hehua.framework.localcache;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

/**
 * @author zhihua
 *
 */
@Component
public class LocationLocalCache extends AbstractLocalCache<Object> {

    /**
     * @param loader
     */
    public LocationLocalCache() {
        super();
    }

    @Override
    public Object load() {
        return "hello" + new Date();
    }

    @Override
    public long getReloadPeriod() {
        return TimeUnit.MINUTES.toMillis(5);
    }

    @Override
    public String key() {
        return "location";
    }

}
