package org.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * # Features
 * <p>
 * - lazy arguments ( avoid `if(logger.isXXXEnabled())` )
 * <p>
 * - fluent api
 * <p>
 * - MDC
 */
public class Slf4jTest {

    @Test
    void testLogApi() {

        var logger = LoggerFactory.getLogger("a.b");
        logger.trace("SLF4J TRACE");
        logger.debug("SLF4J DEBUG");
        logger.info("SLF4J INFO");
        logger.warn("SLF4J WARN");
        logger.error("SLF4J ERROR", new Exception("一个错误"));

    }

    @Test
    void testLogFluentApi() {
        var logger = LoggerFactory.getLogger("a.b");

        // Equals logger.info("SLF4J {} {}","fluent","api")
        logger.atInfo()
                .setMessage("SLF4J {} {}").addArgument("fluent").addArgument("api")
                .log();

        // Equals logger.error("SLF4J {} {}","fluent","api",new Exception("一个错误"))
        logger.atError()
                .setCause(new Exception("一个错误"))
                .setMessage("SLF4J {} {}").addArgument("fluent").addArgument("api")
                .log();

        // ???
        logger.atInfo()
                .setMessage("SLF4J fluent api with key-value")
                .addKeyValue("class_name", getClass().getSimpleName())
                .addKeyValue("method", "testLogFluentApi")
                .log();
    }

    @Test
    void testLoggerIsNotShared() {
        var logger = LoggerFactory.getLogger("a.b");
        var logger2 = LoggerFactory.getLogger("a.b.c");

        assertThat(logger).isNotSameAs(logger2);
    }
}
