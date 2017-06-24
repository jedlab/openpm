package com.jedlab.framework.spring.rest;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author omidp
 *
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface RestWriter
{

}
