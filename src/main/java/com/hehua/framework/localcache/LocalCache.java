/**
 * 
 */
package com.hehua.framework.localcache;

import com.hehua.framework.subscribe.Subscriber;

/**
 * @author zhihua
 *
 */
public interface LocalCache<T> extends ObjectHolder<T>, ObjectLoader<T>, Subscriber {

    public void reload();

    public long getReloadPeriod();
}
