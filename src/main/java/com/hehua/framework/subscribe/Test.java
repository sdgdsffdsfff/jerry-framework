/**
 * 
 */
package com.hehua.framework.subscribe;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.hehua.framework.localcache.LocationLocalCache;

/**
 * @author zhihua
 *
 */
public class Test {

    public static void main(String[] args) {

        final LocationLocalCache localCache = new LocationLocalCache();

        for (int i = 0; i <= 10; i++) {
            System.out.println(localCache.get());
        }

        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                System.out.println(localCache.get());
            }
        }, 0, 1, TimeUnit.SECONDS);

        //        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(new Runnable() {
        //
        //            @Override
        //            public void run() {
        //                ZookeeperPubSubService.getInstance().post(localCache.key(), "reload");
        //            }
        //        }, 0, 3, TimeUnit.SECONDS);

    }
}
