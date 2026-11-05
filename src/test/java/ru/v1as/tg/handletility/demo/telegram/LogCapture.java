package ru.v1as.tg.handletility.demo.telegram;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

public final class LogCapture implements AutoCloseable {

    private final Logger logger;
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

    public LogCapture(Class<?> type) {
        this(LoggerFactory.getLogger(type).getName());
    }

    /** Logger name for {@code HandlerBuilder.named(...)} on a handler of {@code type}. */
    public LogCapture(Class<?> type, String namedSuffix) {
        this(type.getName() + "[" + namedSuffix + "]");
    }

    private LogCapture(String loggerName) {
        this.logger = (Logger) LoggerFactory.getLogger(loggerName);
        appender.start();
        logger.addAppender(appender);
    }

    public boolean warningContains(String part) {
        return appender.list.stream()
                .anyMatch(
                        event ->
                                event.getLevel() == Level.WARN
                                        && event.getFormattedMessage().contains(part));
    }

    public boolean warningHasMdc(String key, String value) {
        return appender.list.stream()
                .anyMatch(
                        event ->
                                event.getLevel() == Level.WARN
                                        && value.equals(event.getMDCPropertyMap().get(key)));
    }

    public boolean infoContains(String part) {
        return appender.list.stream()
                .anyMatch(
                        event ->
                                event.getLevel() == Level.INFO
                                        && event.getFormattedMessage().contains(part));
    }

    public boolean infoHasMdc(String key, String value) {
        return appender.list.stream()
                .anyMatch(
                        event ->
                                event.getLevel() == Level.INFO
                                        && value.equals(event.getMDCPropertyMap().get(key)));
    }

    @Override
    public void close() {
        logger.detachAppender(appender);
    }
}
