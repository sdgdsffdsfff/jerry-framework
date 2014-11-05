/**
 * 
 */
package com.hehua.framework.log;

import java.io.InterruptedIOException;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author zhihua
 *
 */
public class FailoverErrorHandler implements ErrorHandler {

    Appender primary;

    Appender backup;

    @Override
    public void activateOptions() {
    }

    @Override
    public void setLogger(Logger logger) {
    }

    @Override
    public void error(String message, Exception e, int errorCode) {
        error(message, e, errorCode, null);
    }

    @Override
    public void error(String message) {
        error(message, null, 0, null);
    }

    @Override
    public void error(String message, Exception e, int errorCode, LoggingEvent event) {

        if (e instanceof InterruptedIOException) {
            Thread.currentThread().interrupt();
        }

        LogLog.debug("FO: The following error reported: " + message, e);
        LogLog.debug("FO: INITIATING FAILOVER PROCEDURE.");

        if (event != null) {
            backup.doAppend(event);
        }
    }

    @Override
    public void setAppender(Appender primary) {
        LogLog.debug("FB: Setting primary appender to [" + primary.getName() + "].");
        this.primary = primary;
    }

    @Override
    public void setBackupAppender(Appender backup) {
        LogLog.debug("FB: Setting backup appender to [" + backup.getName() + "].");
        this.backup = backup;
    }
}
