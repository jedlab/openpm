package com.jedlab.framework.spring.rest;

import java.io.Serializable;
import java.util.ArrayList;

import org.omidbiz.core.axon.internal.Axon;

@Axon
public class ResultList<E> implements Serializable
{
    private int selectedPageSize;
    private ArrayList<E> resultList;
    private int startPage;
    private int endPage;
    private long resultCount;
    private int totalPage;

    public ResultList()
    {
    }

    public ResultList(int selectedPageSize, ArrayList<E> resultList, int startPage, int endPage, long resultCount, int totalPage)
    {
        this.selectedPageSize = selectedPageSize;
        this.resultList = resultList;
        this.startPage = startPage;
        this.endPage = endPage;
        this.resultCount = resultCount;
        this.totalPage = totalPage;
    }

    public int getTotalPage()
    {
        return totalPage;
    }

    public long getResultCount()
    {
        return resultCount;
    }

    public int getSelectedPageSize()
    {
        return selectedPageSize;
    }

    public void setSelectedPageSize(int selectedPageSize)
    {
        this.selectedPageSize = selectedPageSize;
    }

    public ArrayList<E> getResultList()
    {
        return resultList;
    }

    public void setResultList(ArrayList<E> resultList)
    {
        this.resultList = resultList;
    }

    public int getStartPage()
    {
        return startPage;
    }

    public void setStartPage(int startPage)
    {
        this.startPage = startPage;
    }

    public int getEndPage()
    {
        return endPage;
    }

    public void setEndPage(int endPage)
    {
        this.endPage = endPage;
    }

}