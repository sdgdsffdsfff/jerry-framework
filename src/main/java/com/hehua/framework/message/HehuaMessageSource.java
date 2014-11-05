/**
 * 
 */
package com.hehua.framework.message;

import java.io.IOException;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 这个暂时不支持动态更新，所以暂时废用
 * 
 * @author zhouzhihua <zhihua@afanda.com>
 * @version 1.0 create at Dec 8, 2012 12:06:56 PM
 */
@Deprecated
public class HehuaMessageSource extends ResourceBundleMessageSource {

    private static final String JDBC_RESOURCE_BUNDLE = "JdbcResourceBundle";

    @Autowired
    private MessageDAO messageDAO;

    @Override
    protected ResourceBundle getResourceBundle(String basename, Locale locale) {

        if (basename.startsWith(JDBC_RESOURCE_BUNDLE)) {
            return ResourceBundle.getBundle(basename, locale, getBundleClassLoader(),
                    new Control() {

                        @Override
                        public ResourceBundle newBundle(String baseName, Locale locale,
                                String format, ClassLoader loader, boolean reload)
                                throws IllegalAccessException, InstantiationException, IOException {

                            List<Message> messages = messageDAO.getAll();
                            final Object[][] contents = new Object[messages.size()][2];
                            for (int i = 0; i < messages.size(); i++) {
                                contents[i] = new Object[] { messages.get(i).getCode(),
                                        messages.get(i).getMessage() };
                            }

                            return new ListResourceBundle() {

                                @Override
                                protected Object[][] getContents() {
                                    return contents;
                                }
                            };
                        }

                    });
        }

        return super.getResourceBundle(basename, locale);
    }

    public final String getMessage(String code, Object... args) {
        return getMessage(code, args, Locale.getDefault());
    }

    public final String getMessage(String code) {
        return getMessage(code, null, Locale.getDefault());
    }

    @Override
    protected Object[] resolveArguments(Object[] args, Locale locale) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Number) {
                    args[i] = arg.toString();
                }
            }
        }
        return super.resolveArguments(args, locale);
    }

}
