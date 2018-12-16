package com.jedlab.framework.report;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jedlab.framework.report.JasperDataExporter.BeanAccessor;
import com.jedlab.framework.report.JasperDataExporter.ExportType;

/**
 * @author omidp
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReportAccessor
{

    BeanAccessor accessor() default BeanAccessor.FIELD;
    
}
