/**
 * 
 */
package com.hehua;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.hehua.framework.context.HehuaContext;

/**
 * @author zhihua
 *
 */
@Component()
public class Hehua implements ApplicationContextAware {

    private static HehuaContext context;

    public static void init() {
        new ClassPathXmlApplicationContext("classpath*:/spring/applicationContext*.xml");
    }

    public static HehuaContext getContext() {
        return context;
    }

    public static void main(String[] args) {
        Hehua.init();

        System.out.println(Hehua.getContext().getMessage(404));
        System.out.println(Hehua.getContext().getBean("defaultDataSource", DataSource.class));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = new HehuaContext(applicationContext);
    }

}
