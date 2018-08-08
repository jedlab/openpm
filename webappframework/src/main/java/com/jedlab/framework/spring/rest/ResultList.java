package com.jedlab.framework.spring.rest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.omidbiz.core.axon.internal.Axon;
import org.omidbiz.core.axon.internal.IgnoreElement;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jedlab.framework.json.JacksonView;

@Axon
@JsonIgnoreProperties(value = { "typeName" })
//@JsonFilter("JsonViewFilter")
@JacksonView
public class ResultList<E> implements ParameterizedType
{
    private int selectedPageSize;
    private ArrayList<E> resultList;
    private int startPage;
    private int endPage;
    private long resultCount;
    private int totalPage;
    private final Class<?> clz;

    public ResultList(Class<?> clz)
    {
        this.clz = clz;
    }

    public ResultList(int selectedPageSize, ArrayList<E> resultList, int startPage, int endPage, long resultCount, int totalPage, Class<?> clz)
    {
        this.selectedPageSize = selectedPageSize;
        this.resultList = resultList;
        this.startPage = startPage;
        this.endPage = endPage;
        this.resultCount = resultCount;
        this.totalPage = totalPage;
        this.clz = clz;
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

    @IgnoreElement
    @JsonIgnore
    @Override
    public Type[] getActualTypeArguments()
    {
        return new Type[] { clz };
    }

    @IgnoreElement
    @JsonIgnore
    @Override
    public Type getRawType()
    {
        return resultList.getClass();
    }

    @IgnoreElement
    @JsonIgnore
    @Override
    public Type getOwnerType()
    {
        return null;
    }

    @IgnoreElement
    @JsonIgnore
    public Type getType()
    {
        return this;
    }

   

}