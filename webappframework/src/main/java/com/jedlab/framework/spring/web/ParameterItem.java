package com.jedlab.framework.spring.web;

public class ParameterItem
{

    private String propertyName;
    private ParamOperator operator;
    private Object value;

    public ParameterItem(String propertyName, ParamOperator operator, Object value)
    {
        this.propertyName = propertyName;
        this.operator = operator;
        this.value = value;
    }

    public Object getValue()
    {
        return value;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public ParamOperator getOperator()
    {
        return operator;
    }

}
