package com.jedlab.framework.spring.i18n;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author Omid Pourhadi
 *
 */
@Target(ElementType.FIELD)
@Documented
public @interface MessageSource {

    
    String code() default "";
    String locale() default "en";
    
    
}
