/**
 * 
 */
package com.hehua.framework.subscribe;

/**
 * @author zhihua
 *
 */
public interface Subscriber {

    public String key();

    public void onMessage(Object message);
}
