/**
 * 
 */
package com.hehua.framework.jms;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author zhihua
 *
 */
public class TestJmsSender {

    public static void main(String[] args) {

        System.out.println("=====0");
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath*:/spring/applicationContext-*.xml");

        System.out.println("=====1");

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);

        System.out.println("=====2");

        final JmsApi jmsApi = applicationContext.getBean(JmsApi.class);

        final AtomicInteger id = new AtomicInteger();

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                jmsApi.call("QUEUE_TEST", "" + id.incrementAndGet());

                System.out.println("===xxx" + id.get());
            }
        }, 0, 5, TimeUnit.SECONDS);
        // 
    }
}
