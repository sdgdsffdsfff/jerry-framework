/**
 * 
 */
package com.hehua.framework.jms;

import org.springframework.util.ErrorHandler;

/**
 * @author zhihua
 *
 */
public class SysoutErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable arg0) {
        arg0.printStackTrace();
    }

}
