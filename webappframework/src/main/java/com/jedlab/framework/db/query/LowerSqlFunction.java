package com.jedlab.framework.db.query;

/**
 * @author Omid Pourhadi
 * 
 */
public class LowerSqlFunction implements Function
{

    public LowerSqlFunction()
    {
    }

    @Override
    public String parseColumn(String columnName)
    {
        return "lower(" + columnName + ")";
    }

    @Override
    public Object parseColumnValue(Object columnValue)
    {
        if (columnValue != null)
            return String.valueOf(columnValue).toLowerCase();
        return columnValue;
    }

}
