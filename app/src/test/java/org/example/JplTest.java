package org.example;

import org.junit.jupiter.api.Test;

import java.util.logging.LogManager;

import static java.lang.System.Logger.Level.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Java platform logging api
 *
 * @see https://openjdk.org/jeps/264
 */
public class JplTest {

    /**
     * # Without JUL module installed
     * <p>
     * Default {@link System.Logger} impl is
     * {@link jdk.internal.logger.SimpleConsoleLogger}
     * <p>
     * Default writer is {@link System.err}
     * <p>
     * Default level is {@link System.Logger.Level.INFO}
     * <p>
     * Default formatter is
     *
     * <pre>
     * String.format(format:String,
     *                          now:ZoneDateTime,
     *                          callerInfo:String,
     *                          name:String,
     *                          level:String,
     *                          msg:String,
     *                          throwable:String);
     * </pre>
     *
     * format value get from system property `jdk.system.logger.format`
     * <p>
     * and the default value is `%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp
     * %2$s%n%4$s: %5$s%6$s%n`
     * <p>
     * <p>
     * # With JUL module installed
     * <p>
     * {@link System.Logger} -> {@link java.util.logging.Logger}
     * <p>
     * Default {@link System.Logger} impl is {@link LoggingProviderImpl.JULWrapper}
     * <p>
     * Default {@link java.lang.System.LoggerFinder} impl is
     * {@link sun.util.logging.LoggingProviderImpl}
     */
    @Test
    void testLogApi() {
        var logger = System.getLogger("a.b.c");
        logger.log(DEBUG, "JPL DEBUG");
        logger.log(INFO, "JPL INFO");
        logger.log(WARNING, "JPL WARNING");
        logger.log(ERROR, "JPL ERROR", new Exception("一个错误"));
    }

    @Test
    void testLoggerIsJULWrapper() {
        var logger = System.getLogger("a.b.c");
        assertThat(logger.getClass().getName()).contains("JULWrapper");
    }

    @Test
    void testCustomJulConfig() throws Exception {
        // JUL re-read config file from specify path
        System.setProperty("java.util.logging.config.file", "src/test/resources/logging.properties");
        var logManager = LogManager.getLogManager();
        logManager.readConfiguration();

        testLogApi();
    }

    @Test
    void testLoggerIsNotShared() {
        var logger = System.getLogger("a.b");
        var logger2 = System.getLogger("a.b.c");

        assertThat(logger).isNotSameAs(logger2);
    }
}
