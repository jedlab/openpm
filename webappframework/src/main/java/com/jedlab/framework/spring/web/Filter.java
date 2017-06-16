package com.jedlab.framework.spring.web;

import java.io.Serializable;
import java.util.Map;

import com.jedlab.framework.db.QueryMapper;

public interface Filter extends Serializable
{

    default public boolean hasFilter()
    {
        Map<String, Object> filterMap = QueryMapper.filterMap(this);
        return filterMap.size() > 0;
    }

}
