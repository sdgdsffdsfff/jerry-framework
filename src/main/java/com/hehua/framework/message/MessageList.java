/**
 * 
 */
package com.hehua.framework.message;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.hehua.commons.Transformers;

/**
 * @author zhihua
 *
 */
public class MessageList {

    private final List<Message> messageList;

    private final Map<Integer, Message> messageMapById;

    private final Map<String, Message> messageMapByCode;

    /**
     * @param messageList
     */
    public MessageList(List<Message> messageList) {
        super();
        this.messageList = Collections.unmodifiableList(messageList);

        this.messageMapById = Collections.unmodifiableMap(Transformers.transformAsOneToOneMap(
                messageList, new Function<Message, Integer>() {

                    @Override
                    public Integer apply(Message f) {
                        return f.getId();
                    }
                }));

        this.messageMapByCode = Collections.unmodifiableMap(Transformers.transformAsOneToOneMap(
                messageList, new Function<Message, String>() {

                    @Override
                    public String apply(Message f) {
                        return f.getCode();
                    }
                }));
    }

    public Message getById(int id) {
        return messageMapById.get(id);
    }

    public Message getByCode(String code) {
        return messageMapByCode.get(code);
    }

    public List<Message> getAll() {
        return messageList;
    }

}
