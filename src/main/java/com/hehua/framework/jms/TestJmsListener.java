/**
 * 
 */
package com.hehua.framework.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestJmsListener implements MessageListener {

    private Logger logger = Logger.getLogger(getClass());

    public TestJmsListener() {
    }

    @Autowired(required = true)
    private JmsTemplate jmsTemplate;

    public void invoke(Object args) {
        System.out.println("receive mesage : " + args);
        int id = Integer.parseInt(args.toString());
        if (id == 2) {
            throw new RuntimeException("error");
        }
    }

    @Override
    public void onMessage(Message arg0) {

        if (arg0 instanceof ObjectMessage) {
            try {
                Object args = ((ObjectMessage) arg0).getObject();
                invoke(args);
            } catch (JMSException e1) {
                logger.error("Ops.", e1);
                throw new RuntimeException(e1);
            }
        } else {
            logger.error(getClass().getSimpleName() + " receive err type of message:"
                    + arg0.getClass().getSimpleName());
        }

    }
}
