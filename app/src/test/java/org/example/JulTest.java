package org.example;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.util.logging.Level.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * # Features:
 * <p>
 * - lazy arguments ( avoid `if(logger.isXXXEnabled())` )
 */
public class JulTest {

    @Test
    void testLogApi() {
        var logger = Logger.getLogger("a.b");
        logger.log(FINEST, "JUL FINEST");
        logger.log(FINER, "JUL FINER");
        logger.log(FINE, "JUL FINE");
        logger.log(CONFIG, "JUL CONFIG");
        logger.log(INFO, "JUL INFO");
        logger.log(WARNING, "JUL WARNING");
        logger.log(SEVERE, "JUL SEVERE", new Exception("一个错误"));
    }

    @Test
    void testLogMethodEntryApi() throws IOException {
        customConfig();

        var logger = Logger.getLogger("a.b");
        logger.entering(getClass().getName(), "testLogMethodEntryApi");
        logger.exiting(getClass().getName(), "testLogMethodEntryApi");
        logger.throwing(getClass().getName(), "testLogMethodEntryApi", new Exception("一个错误"));
    }

    /**
     * {@link java.util.logging.LogManager} is a global singleton and responsible
     * for managing configuration, loggers and listeners
     * <p>
     * {@link java.util.logging.LogManager.LoggerContext} is a map for managing
     * loggers
     * <p>
     * {@link java.util.logging.Logger} is responsible for publishing
     * {@link java.util.logging.LogRecord}
     * to {@link java.util.logging.Handler}
     * <p>
     * {@link java.util.logging.Formatter} is responsible for formatting
     * {@link java.util.logging.LogRecord}
     */
    @Test
    void testCustomConfig() throws IOException {
        customConfig();

        testLogApi();
    }

    static void customConfig() throws IOException {
        // re-read config file from specify path
        System.setProperty("java.util.logging.config.file", "src/test/resources/logging.properties");
        var logManager = LogManager.getLogManager();
        logManager.readConfiguration();
    }

    @Test
    void testLoggerIsNotShared() {
        var logger = Logger.getLogger("a.b");
        var logger2 = Logger.getLogger("a.b.c");

        assertThat(logger).isNotSameAs(logger2);
    }

    @Test
    void testParentLogger() {
        var logger = Logger.getLogger("a.b");
        var logger2 = Logger.getLogger("a.b.c");
        var rootLogger = Logger.getLogger("");

        assertThat(logger2.getParent()).isSameAs(logger);
        assertThat(logger.getParent()).isSameAs(rootLogger);
    }

    @Test
    void testHandlersIsShared() throws IOException {
        customConfig();

        var logger = Logger.getLogger("a.b");
        var logger2 = Logger.getLogger("a.b.c");
        var rootLogger = Logger.getLogger("");

        var handlers = getHandlersHierarchically(logger);
        var handlers2 = getHandlersHierarchically(logger2);

        // System.out.println("handlers = " + handlers);
        // System.out.println("handlers2 = " + handlers2);

        assertThat(handlers).isEqualTo(handlers2);
        assertThat(List.of(rootLogger.getHandlers())).isEqualTo(handlers2);
    }

    List<Handler> getHandlersHierarchically(Logger logger) {
        var list = new ArrayList<Handler>();
        var l = logger;
        for (; l != null;) {
            list.addAll(Arrays.asList(l.getHandlers()));
            if (l.getUseParentHandlers())
                l = l.getParent();
            else
                break;
        }
        return list;
    }
}
