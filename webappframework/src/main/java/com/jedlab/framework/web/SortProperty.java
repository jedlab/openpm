package com.jedlab.framework.web;

import org.primefaces.model.SortOrder;

public class SortProperty
{
    private final String name;
    private final SortOrder sortOrder;

    public SortProperty(String name, SortOrder sortOrder)
    {
        this.name = name;
        this.sortOrder = sortOrder;
    }

    public String getName()
    {
        return name;
    }

    public SortOrder getSortOrder()
    {
        return sortOrder;
    }

}