/**
 * 
 */
package com.hehua.framework.subscribe;

/**
 * @author zhihua
 *
 */
public interface PubSubService {

    public void post(String key, String message);

    public void register(String key, Subscriber subscriber);
}
