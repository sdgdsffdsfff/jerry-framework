/**
 * 
 */
package com.hehua.framework.lock;

import org.junit.Test;

/**
 * @author zhihua
 *
 */
public class RedisLockTest {

    @Test
    public void test() {
        System.out.println(Thread.currentThread().isInterrupted());
        System.out.println("====1");
        Thread.currentThread().interrupt();

        System.out.println(Thread.currentThread().isInterrupted());
        System.out.println("====2");
    }

}
