package shared;

import org.apache.log4j.Logger;

public class Log {

    private final Logger logger;

    public Log(String name) {
        this.logger = Logger.getLogger(name);
    }

    public Log(Class c) {
        this.logger = Logger.getLogger(c);
    }

    public void fatal(Object message, boolean stackTrace) {
        if (stackTrace) {
            logger.fatal(message, new Throwable());
        } else {
            logger.fatal(message);
        }
    }

    public void error(Object message, boolean stackTrace) {
        if (stackTrace) {
            logger.error(message, new Throwable());
        } else {
            logger.error(message);
        }
    }

    public void warn(Object message, boolean stackTrace) {
        if (stackTrace) {
            logger.warn(message, new Throwable());
        } else {
            logger.warn(message);
        }
    }

    public void info(Object message, boolean stackTrace) {
        if (logger.isInfoEnabled()) {
            if (stackTrace) {
                logger.info(message, new Throwable());
            } else {
                logger.info(message);
            }
        }
    }

    public void debug(Object message, boolean stackTrace) {
        if (logger.isDebugEnabled()) {
            if (stackTrace) {
                logger.debug(message, new Throwable());
            } else {
                logger.debug(message);
            }
        }
    }

    public void trace(Object message, boolean stackTrace) {
        if (logger.isTraceEnabled()) {
            if (stackTrace) {
                logger.trace(message, new Throwable());
            } else {
                logger.trace(message);
            }
        }
    }
}
