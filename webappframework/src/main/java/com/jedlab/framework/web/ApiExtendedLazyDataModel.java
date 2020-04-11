package com.jedlab.framework.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.jedlab.framework.db.EntityModel;

public abstract class ApiExtendedLazyDataModel<E extends EntityModel> extends LazyDataModel<E>
{
    private Number count;

    public ApiExtendedLazyDataModel()
    {
    }

    @Override
    public List<E> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters)
    {
        if (filters != null)
            filters.values().removeIf(Objects::isNull);
        ApiListResponse<E> lazyLoadRsp = lazyLoad(first, pageSize, Arrays.asList(new SortProperty(sortField, sortOrder)), filters);
        this.count = lazyLoadRsp.getResultCount();
        if (count == null)
            return new ArrayList<E>();
        setRowCount(count.intValue());
        List<E> resultList = lazyLoadRsp.getResultList();
        if (resultList == null)
            return new ArrayList<>();
        return resultList;
    }

    @Override
    public List<E> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters)
    {
        List<SortProperty> sorts = new ArrayList<SortProperty>();
        if (multiSortMeta != null)
        {
            multiSortMeta.forEach(item -> sorts.add(new SortProperty(item.getSortField(), item.getSortOrder())));
        }
        if (filters != null)
            filters.values().removeIf(Objects::isNull);
        ApiListResponse<E> lazyLoadRsp = lazyLoad(first, pageSize, sorts, filters);
        this.count = lazyLoadRsp.getResultCount();
        if (count == null)
            return new ArrayList<E>();
        setRowCount(count.intValue());
        List<E> resultList = lazyLoadRsp.getResultList();
        if (resultList == null)
            return new ArrayList<>();
        return resultList;
    }

    @Override
    public int getRowCount()
    {
        if (this.count == null)
            return 0;
        return this.count.intValue();
    }

    @Override
    public E getRowData(String rowKey)
    {
        List<E> res = (List<E>) getWrappedData();
        List<E> wrappedData = new ArrayList<>(res);
        for (E e : wrappedData)
        {
            if (e.getId() instanceof Long)
            {
                if (e.getId().equals(Long.valueOf(rowKey)))
                    return e;
            }
            else
            {
                if (e.equals(rowKey))
                    return e;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(E instance)
    {
        if (instance == null)
            return null;
        return instance.getId();
    }

    protected abstract ApiListResponse<E> lazyLoad(int first, int pageSize, List<SortProperty> sortFields, Map<String, Object> filters);

}