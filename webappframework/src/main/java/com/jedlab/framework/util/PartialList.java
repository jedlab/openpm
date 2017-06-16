package com.jedlab.framework.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import com.google.common.collect.Lists;

public abstract class PartialList<T>
{

    private final List<T> resultList;
    private static final int PARTIAL_SIZE = 2;
    private int partialSize = PARTIAL_SIZE;

    public PartialList(List<T> resultList)
    {
        if(resultList == null)
            throw new IllegalArgumentException("resultList can not be null");
        this.resultList = resultList;
    }

    public PartialList(List<T> resultList, int partialSize)
    {
        if(resultList == null)
            throw new IllegalArgumentException("resultList can not be null");
        if(partialSize > resultList.size())
            throw new IllegalArgumentException("partial size should be less than result list");
        this.resultList = resultList;
        this.partialSize = partialSize;
    }

    public void process()
    {
        
        List<List<T>> partition = Lists.partition(this.resultList, this.partialSize);
        for (List<T> item : partition)
        {
            Runnable doInThread = doInThread(item);
            Thread thread = new Thread(doInThread);
            UncaughtExceptionHandler handler = getUncaughtExceptionHandler();
            if(handler != null)
                thread.setUncaughtExceptionHandler(handler);
            thread.start();
        }
    }

    protected UncaughtExceptionHandler getUncaughtExceptionHandler()
    {
        return null;
    }

    public abstract Runnable doInThread(List<T> item);

}
