package com.jedlab.framework.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DTO {

    Class<? extends DTOPropertyMapper> mapper();

    Class<?> value();

    boolean springEnabled() default false;

}