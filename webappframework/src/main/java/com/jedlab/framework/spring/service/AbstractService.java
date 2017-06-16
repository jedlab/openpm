package com.jedlab.framework.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

import com.jedlab.framework.exceptions.ServiceException;
import com.jedlab.framework.spring.dao.AbstractDAO;
import com.jedlab.framework.util.CollectionUtil;
import com.jedlab.framework.web.ExtendedLazyDataModel.SortProperty;

/**
 * @author Omid Pourhadi
 *
 */
public abstract class AbstractService<E>
{

    @Autowired
    protected EntityManager entityManager;

    public abstract AbstractDAO<E> getDao();

    public List<E> load(int first, int pageSize, List<com.jedlab.framework.web.ExtendedLazyDataModel.SortProperty> sortFields,
            Map<String, Object> filters, Specification<E> spec)
    {
        // TODO : apply filters
        Page<E> result = null;
        Sort sort = applySort(sortFields);
        if (spec != null)
        {
            if (sort != null)
                result = getDao().findAll(spec, new PageRequest(first / pageSize, pageSize, sort));
            else
                result = getDao().findAll(spec, new PageRequest(first / pageSize, pageSize));
        }
        else
        {
            if (sort != null)
                result = getDao().findAll(new PageRequest(first / pageSize, pageSize, sort));
            else
                result = getDao().findAll(new PageRequest(first / pageSize, pageSize));
        }
        return result.getContent();
    }

    private Sort applySort(List<SortProperty> sortFields)
    {
        if (CollectionUtil.isEmpty(sortFields))
            return null;
        List<Order> orders = new ArrayList<Sort.Order>();
        for (SortProperty sp : sortFields)
        {
            if (SortOrder.ASCENDING.equals(sp.getSortOrder()))
                orders.add(new Order(Direction.ASC, sp.getName()));
            else
                orders.add(new Order(Direction.DESC, sp.getName()));
        }
        if (CollectionUtil.isEmpty(orders))
            return null;
        return new Sort(orders);
    }

    public Long count(Specification<E> spec)
    {
        if (spec != null)
            return getDao().count(spec);
        else
            return getDao().count();
    }

    public void insert(E entity)
    {
        beforeInsert(entity);
        getDao().save(entity);
        afterInsert(entity);
    }

    public void update(E entity)
    {
        beforeUpdate(entity);
        getDao().save(entity);
        afterUpdate(entity);
    }

    protected void afterUpdate(E entity)
    {

    }

    protected void beforeUpdate(E entity)
    {

    }

    private void metaData(Class<E> domainClass)
    {
        Metamodel metamodel = entityManager.getMetamodel();
        ManagedType<E> type = metamodel.managedType(domainClass);
        if (!(type instanceof IdentifiableType))
        {
            throw new ServiceException("The given domain class does not contain an id attribute!");
        }
        IdentifiableType<E> source = (IdentifiableType<E>) type;
        if (source.hasSingleIdAttribute())
        {
            SingularAttribute<? super E, ?> idAttribute = source.getId(source.getIdType().getJavaType());
        }
        else
        {
            throw new ServiceException("unsupported operator");
        }
    }

    protected void afterInsert(E entity)
    {

    }

    protected void beforeInsert(E entity)
    {

    }

    public E findById(Class<E> clz, Object id)
    {
        return entityManager.find(clz, id);
    }

}
