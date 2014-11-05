/**
 * 
 */
package com.hehua.framework.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.NoSuchMessageException;

import com.hehua.framework.message.MessageManager;

/**
 * @author zhihua
 *
 */
public class HehuaContext {

    private static final Log logger = LogFactory.getLog(HehuaContext.class);

    private final ApplicationContext applicationContext;

    /**
     * @param applicationContext
     */
    public HehuaContext(ApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

    public <T> T getBean(String beanName, Class<T> beanClass) {
        return applicationContext.getBean(beanName, beanClass);
    }

    public String getMessage(String code, Object... objects) {
        try {
            return getBean(MessageManager.class).getMessage(code, objects);
        } catch (NoSuchMessageException e) {
            logger.warn(String.format("message#%s is miss", code));
            return null;
        }
    }

    public String getMessage(int id, Object... objects) {
        try {
            return getBean(MessageManager.class).getMessage(id, objects);
        } catch (NoSuchMessageException e) {
            logger.warn(String.format("message#%s is miss", id));
            return null;
        }
    }

}
