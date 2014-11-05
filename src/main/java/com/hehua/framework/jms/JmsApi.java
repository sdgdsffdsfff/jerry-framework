package com.hehua.framework.jms;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ScheduledMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class JmsApi implements ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(JmsApi.class);

    private ApplicationContext applicationContext = null;

    @Resource(name = "jmsTemplate")
    private JmsTemplate jmsTemplate;

    public JmsApi() {
    }

    public void call(String serviceQueue, final Serializable args) {
        this.call(serviceQueue, args, 0l);
    }

    public void call(String serviceQueue, final Serializable args, final long delay) {
        Queue service = (Queue) applicationContext.getBean(serviceQueue);
        if (service == null) {
            throw new RuntimeException("Could not found " + serviceQueue + " in spring.");
        }

        try {
            jmsTemplate.send(service, new MessageCreator() {

                @Override
                public Message createMessage(Session session) throws JMSException {
                    ObjectMessage message = session.createObjectMessage(args);
                    if (delay != 0) {
                        message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
                    }
                    return message;
                }
            });
        } catch (JmsException e) {
            throw new RuntimeException("jms send err", e);
        } catch (Exception e) {
            throw new RuntimeException("args serialization err", e);
        }
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void publish(String serviceTopic, final Serializable args) {
        Topic topic = (Topic) applicationContext.getBean(serviceTopic);
        if (topic == null) {
            throw new RuntimeException("Could not found " + serviceTopic + " in spring.");
        }

        try {
            jmsTemplate.send(topic, new MessageCreator() {

                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(args);
                }
            });
        } catch (JmsException e) {
            throw new RuntimeException("jms send err", e);
        } catch (Exception e) {
            throw new RuntimeException("args serialization err", e);
        }
    }

}
