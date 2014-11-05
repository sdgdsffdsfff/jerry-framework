/**
 * 
 */
package com.hehua.framework.message;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hehua.framework.localcache.AbstractLocalCache;

/**
 * @author zhihua
 *
 */
@Component
public class MessageListLocalCache extends AbstractLocalCache<MessageList> {

    @Autowired
    private MessageDAO messageTemplateDAO;

    @Override
    public MessageList load() {
        List<Message> messageList = messageTemplateDAO.getAll();
        return new MessageList(messageList);
    }

    @Override
    public String key() {
        return "message";
    }

    @Override
    public long getReloadPeriod() {
        return TimeUnit.MINUTES.toMillis(1);
    }

}
