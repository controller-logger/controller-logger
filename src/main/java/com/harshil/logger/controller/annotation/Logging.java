package com.harshil.logger.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to turn on logging for a single method when applied on method level or for all methods of a
 * class when applied on class level.
 *
 * <p>
 * Note that when applied on method level it overrides settings configured on class-level.
 *
 * <p>
 * This can be used together with {@link NoLogging} to gain fine-grain control on logging.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {
}
