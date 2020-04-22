package com.jedlab.framework.util;

import java.util.Date;

public class DateUtil
{

    public static Date toDate(Object value)
    {
        if (value instanceof Date)
            return (Date) value;
        else if (value instanceof Long)
            return new Date((Long) value);
        throw new IllegalArgumentException("cannot cast " + value + " to java.util.Date");
    }
}