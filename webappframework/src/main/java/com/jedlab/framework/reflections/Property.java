package com.jedlab.framework.reflections;

import java.io.Serializable;

public class Property implements Serializable
{
    private String columnName;
    private String fieldName;

    public Property(String columnName, String fieldName)
    {
        this.columnName = columnName;
        this.fieldName = fieldName;
    }

    public String getColumnName()
    {
        return columnName;
    }

    public String getFieldName()
    {
        return fieldName;
    }
}
