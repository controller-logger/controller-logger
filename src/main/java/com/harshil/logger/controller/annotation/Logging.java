package com.harshil.logger.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class and method level annotation to turn on automatic logging.
 * Adding it to class or method enables logging for it. Annotation on method takes precedence over that on class.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {
}
