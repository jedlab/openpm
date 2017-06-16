package com.jedlab.framework.report;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.jedlab.framework.report.JasperDataExporter.ExportType;

public class ReportItem implements Serializable
{
    private int order;
    private Class<?>[] interceptor;
    private int width;
    private String title;
    private String fieldName;
    private Class<?> fieldType;
    private FieldType type;
    private ExportType[] exportTypes;

    public ReportItem(int order, Class<?>[] interceptor, int width, String title, String fieldName, Class<?> fieldType, FieldType type,
            ExportType[] exportTypes)
    {
        this.order = order;
        this.interceptor = interceptor;
        this.width = width;
        this.title = title;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.type = type;
        this.exportTypes = exportTypes;
    }

    public ExportType[] getExportTypes()
    {
        return exportTypes;
    }

    public FieldType getType()
    {
        return type;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public int getOrder()
    {
        return order;
    }

    public List<Class<?>> getInterceptor()
    {
        return Arrays.asList(interceptor);
    }

    public int getWidth()
    {
        return width;
    }

    public String getTitle()
    {
        return title;
    }

    public Class<?> getFieldType()
    {
        return fieldType;
    }

}