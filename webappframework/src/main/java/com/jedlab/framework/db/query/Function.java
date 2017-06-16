package com.jedlab.framework.db.query;

/**
 * @author omid
 *
 */
public interface Function
{

    public String parseColumn(String columnName);
    public Object parseColumnValue(Object columnValue);
    
}
