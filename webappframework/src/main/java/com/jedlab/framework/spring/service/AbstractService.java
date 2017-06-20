package com.jedlab.framework.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

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

    @PersistenceContext
    protected EntityManager entityManager;

    public abstract AbstractDAO<E> getDao();

    @Transactional(readOnly=true)
    public List<E> load(int first, int pageSize, List<com.jedlab.framework.web.ExtendedLazyDataModel.SortProperty> sortFields,
            Map<String, Object> filters, Class<E> clz, Restriction restriction)
    {
        // The JPA spec does not allow a alias to be given to a fetch join, so
        // we use hibernate seesion to ignore the spec
        List<E> result = new ArrayList<>();
        Session hibernateSession = (Session) entityManager.getDelegate();
        Criteria criteria = hibernateSession.createCriteria(clz);
        if(restriction != null)
            restriction.applyFilter(criteria);
        if (CollectionUtil.isNotEmpty(sortFields))
        {
            sortFields.forEach(item -> {
                if (SortOrder.ASCENDING.equals(item.getSortOrder()))
                    criteria.addOrder(org.hibernate.criterion.Order.asc(item.getName()));
                else
                    criteria.addOrder(org.hibernate.criterion.Order.desc(item.getName()));
            });
        }
        criteria.setFirstResult(first);
        criteria.setMaxResults(pageSize);
        result = criteria.list();
        return result;
    }
    
    

    protected Sort applySort(List<SortProperty> sortFields)
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

    @Transactional(readOnly=true)
    public Long count(Class<E> clz, Restriction restriction)
    {
        Session hibernateSession = (Session) entityManager.getDelegate();
        Criteria criteria = hibernateSession.createCriteria(clz);
        if(restriction != null)
            restriction.applyFilter(criteria);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    @Transactional
    public void insert(E entity)
    {
        beforeInsert(entity);
        getDao().save(entity);
        afterInsert(entity);
    }

    @Transactional
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

    @Transactional(readOnly=true)
    public E findById(Class<E> clz, Object id)
    {
        return entityManager.find(clz, id);
    }

    public Iterable<E> findAll()
    {
        return getDao().findAll();
    }

    public Iterable<E> findAll(Specification<E> spec)
    {
        return getDao().findAll(spec);
    }

}
