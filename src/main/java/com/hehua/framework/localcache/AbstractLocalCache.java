/**
 * 
 */
package com.hehua.framework.localcache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.hehua.framework.subscribe.ZookeeperPubSubService;

/**
 * @author zhihua
 *
 */
public abstract class AbstractLocalCache<T> implements LocalCache<T> {

    private final AtomicReference<T> objectRef;

    private final AtomicBoolean init;

    /**
     * @param loader
     */
    public AbstractLocalCache() {
        super();
        this.objectRef = new AtomicReference<>();
        this.init = new AtomicBoolean(false);
        if (!isLazy()) {
            init();
        }
    }

    protected void init() {
        if (init.get()) {
            return;
        }

        synchronized (init) {
            if (init.get()) {
                return;
            }
            initInternal();
            LocalCacheManager.getInstance().register(this);
            init.set(true);
        }
    }

    protected void initInternal() {
        T object = load();
        set(object);
    }

    @Override
    public void onMessage(Object message) {
        reload();
    }

    @Override
    public T get() {
        init();
        return objectRef.get();
    }

    @Override
    public void reload() {
        T newValue = load();
        set(newValue);
    }

    public void set(T object) {
        this.objectRef.set(object);
    }

    @Override
    public long getReloadPeriod() {
        return TimeUnit.DAYS.toMillis(1);
    }

    public boolean isLazy() {
        return true;
    }

    public void postReloadMessage() {
        reload();
        ZookeeperPubSubService.getInstance().post(key(), "reload");
    }

}
