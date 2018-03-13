package com.jedlab.framework.spring.rest;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RUNTIME)
@Documented
public @interface RestReader {

}
