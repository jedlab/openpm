package com.jedlab.framework.report;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jedlab.framework.report.JasperDataExporter.ExportType;

/**
 * @author omidp
 *
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReportField
{

    /**
     * fieldName
     * @return
     */
    String msg();
    
    int width() default 150;
    
    FieldType type() default FieldType.PRIMITIVE;
    
    int order() default 0;
    
    /**
     * this class must implement GenerateReportInterceptor interface if field type is CUSTOM
     * @return
     */
    Class<?>[] interceptor() default void.class;
    
    ExportType[] exportTypes() default {ExportType.XLS, ExportType.PDF, ExportType.INLINEPDF};
    
}
