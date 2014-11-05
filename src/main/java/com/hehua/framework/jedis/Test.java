/**
 * 
 */
package com.hehua.framework.jedis;

import java.util.List;

/**
 * @author zhihua
 *
 */
public class Test {

    public static void main(String[] args) {
        PoolableJedis jedis = PoolableJedisManager.getJedis("sms");

        while (true) {

            try {
                List<String> brpop = jedis.brpop(10, "keylist1");
                if (brpop == null) {
                    System.out.println("got result == null");
                    continue;
                }

                System.out.println(brpop);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //        for (int i = 0; i <= 100000; i++) {
        //            long s = System.currentTimeMillis();
        //            jedis.set("key1", "value1");
        //            System.out.println(jedis.get("key1"));
        //            System.out.println((System.currentTimeMillis() - s));
        //        }

    }
}
