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

public abstract class ExtendedLazyDataModel<E extends EntityModel> extends LazyDataModel<E>
{

    private Number count;

    public ExtendedLazyDataModel()
    {
    }

    @Override
    public List<E> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters)
    {
        this.count = rowCount(filters);
        if (count == null)
            return new ArrayList<E>();
        setRowCount(count.intValue());
        if (count.intValue() > 0)
        {
            if (filters != null)
                filters.values().removeIf(Objects::isNull);
            List<E> load = lazyLoad(first, pageSize, Arrays.asList(new SortProperty(sortField, sortOrder)), filters);
            return load;
        }
        return new ArrayList<E>();
    }

    @Override
    public List<E> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters)
    {
        List<SortProperty> sorts = new ArrayList<ExtendedLazyDataModel.SortProperty>();
        if (multiSortMeta != null)
        {
            multiSortMeta.forEach(item -> sorts.add(new SortProperty(item.getSortField(), item.getSortOrder())));
        }
        this.count = rowCount(filters);
        if (count == null)
            return new ArrayList<E>();
        setRowCount(count.intValue());
        if (count.intValue() > 0)
        {
            if (filters != null)
                filters.values().removeIf(Objects::isNull);
            List<E> load = lazyLoad(first, pageSize, sorts, filters);
            return load;
        }
        return new ArrayList<E>();
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
    };

    public static class SortProperty
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

    protected abstract List<E> lazyLoad(int first, int pageSize, List<SortProperty> sortFields, Map<String, Object> filters);

    protected abstract Number rowCount(Map<String, Object> filters);

}
