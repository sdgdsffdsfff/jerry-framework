/**
 * 
 */
package com.hehua.framework.log;

/**
 * @author zhihua
 *
 */
public class FlumeSourceTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        //        RpcClient thriftInstance = RpcClientFactory.getThriftInstance("10.10.1.200", 6601);
        //        try {
        //            for (int i = 0; i < 1000; i++) {
        //                SimpleEvent event = new SimpleEvent();
        //                event.setBody(("test" + 1).getBytes());
        //                thriftInstance.append(event);
        //            }
        //            thriftInstance.close();
        //            System.out.println("dodo");
        //        } catch (EventDeliveryException e) {
        //            e.printStackTrace();
        //        }

        for (int i = 0; i < 100000; i++) {
            LogService.getInstance().log("default", "wwww");
        }

        System.out.println("xxx");
    }

}
