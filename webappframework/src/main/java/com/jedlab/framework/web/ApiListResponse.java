package com.jedlab.framework.web;

import java.util.ArrayList;
import java.util.List;

public class ApiListResponse<E>
{
    private Long resultCount;
    private List<E> resultList = new ArrayList<>();

    public ApiListResponse()
    {
    }

    public ApiListResponse(Long resultCount, List<E> resultList)
    {
        this.resultCount = resultCount;
        this.resultList = resultList;
    }

    public Long getResultCount()
    {
        return resultCount;
    }

    public void setResultCount(Long resultCount)
    {
        this.resultCount = resultCount;
    }

    public List<E> getResultList()
    {
        return resultList;
    }

    public void setResultList(List<E> resultList)
    {
        this.resultList = resultList;
    }

}