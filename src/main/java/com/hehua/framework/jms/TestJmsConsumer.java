/**
 * 
 */
package com.hehua.framework.jms;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * @author zhihua
 *
 */
public class TestJmsConsumer {

    /**
     * @param args
     */
    public static void main(String[] args) {

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath:/spring/*.xml");

        DefaultMessageListenerContainer messageListenerContainer = applicationContext
                .getBean(DefaultMessageListenerContainer.class);

        messageListenerContainer.start();

    }

}
