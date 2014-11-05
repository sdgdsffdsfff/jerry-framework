/**
 * 
 */
package com.hehua.framework.localcache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.hehua.framework.subscribe.ZookeeperPubSubService;

/**
 * @author zhihua
 *
 */
public class LocalCacheManager {

    private static final LocalCacheManager INSTANCE = new LocalCacheManager();

    public static LocalCacheManager getInstance() {
        return INSTANCE;
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);

    private final ConcurrentHashMap<String, LocalCache<?>> localCacheMap = new ConcurrentHashMap<>();

    private LocalCacheManager() {
    }

    public void register(LocalCache<?> localCache) {

        LocalCache<?> oldOne = localCacheMap.putIfAbsent(localCache.key(), localCache);
        if (oldOne != null) {
            throw new RuntimeException("duplicate cache with same key, old="
                    + oldOne.getClass().getName() + ", new=" + localCache.getClass().getName());
        }

        scheduleReload(localCache);
        scribeReloadMessage(localCache);
    }

    public void scheduleReload(final LocalCache<?> localCache) {
        scheduler.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                localCache.reload();
            }
        }, localCache.getReloadPeriod(), localCache.getReloadPeriod(), TimeUnit.MILLISECONDS);
    }

    public void scribeReloadMessage(final LocalCache<?> localCache) {
        ZookeeperPubSubService.getInstance().register(localCache.key(), localCache);
    }
}
