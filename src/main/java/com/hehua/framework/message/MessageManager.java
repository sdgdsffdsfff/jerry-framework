package com.hehua.framework.message;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

@Component("messageTemplateManager")
public class MessageManager implements InitializingBean {

    @Autowired
    private MessageListLocalCache messageTemplateListLocalCache;

    public MessageManager() {

    }

    public Message getById(int id) {
        return messageTemplateListLocalCache.get().getById(id);
    }

    public Message getByCode(String code) {
        return messageTemplateListLocalCache.get().getByCode(code);
    }

    public List<Message> getAll() {
        return messageTemplateListLocalCache.get().getAll();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public String getMessage(String code, Object... objects) throws NoSuchMessageException {
        Message messageTemplate = getByCode(code);
        if (messageTemplate == null) {
            throw new NoSuchMessageException(code);
        }
        return messageTemplate.format(objects);
    }

    public String getMessage(int id, Object... objects) throws NoSuchMessageException {
        Message messageTemplate = getById(id);
        if (messageTemplate == null) {
            throw new NoSuchMessageException("" + id);
        }
        return messageTemplate.format(objects);
    }
}
