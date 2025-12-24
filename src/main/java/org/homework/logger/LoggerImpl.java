package org.homework.logger;

import org.homework.di.annotations.Register;
import org.slf4j.LoggerFactory;

@Register
public class LoggerImpl implements Logger {
    private final org.slf4j.Logger logger;

    public LoggerImpl() {
        this.logger = LoggerFactory.getLogger(LoggerImpl.class);
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }
}