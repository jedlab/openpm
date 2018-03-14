package com.jedlab.framework.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import com.jedlab.framework.exceptions.ServiceException;
import com.jedlab.framework.spring.dao.AbstractDAO;
import com.jedlab.framework.util.CollectionUtil;
import com.jedlab.framework.util.StringUtil;
import com.jedlab.framework.web.ExtendedLazyDataModel.SortProperty;

/**
 * @author Omid Pourhadi
 *
 */
public abstract class AbstractService<E>
{

    @PersistenceContext(unitName = "entityManagerFactory")
    protected EntityManager entityManager;

    public abstract AbstractDAO<E> getDao();

    @Transactional(readOnly = true)
    @Deprecated
    public List<E> load(int first, int pageSize, List<com.jedlab.framework.web.ExtendedLazyDataModel.SortProperty> sortFields,
            Map<String, Object> filters, Class<E> clz, Restriction restriction)
    {
        // The JPA spec does not allow a alias to be given to a fetch join, so
        // we use hibernate seesion to ignore the spec
        List<E> result = new ArrayList<>();
        Session hibernateSession = (Session) getEntityManager().getDelegate();
        Criteria criteria = hibernateSession.createCriteria(clz);
        if (restriction != null)
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
        // set 0 for unlimited
        if (pageSize > 0)
            criteria.setMaxResults(pageSize);

        if (getHints() != null)
        {
            for (Map.Entry<String, String> me : getHints().entrySet())
            {
                criteria.setComment(me.getValue());
            }
        }

        result = criteria.list();
        return result;
    }

    protected Map<String, String> getHints()
    {
        return null;
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

    @Transactional(readOnly = true)
    @Deprecated
    public Long count(Class<E> clz, Restriction restriction)
    {
        Session hibernateSession = (Session) getEntityManager().getDelegate();
        Criteria criteria = hibernateSession.createCriteria(clz);
        if (restriction != null)
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

    public void delete(Long id)
    {
        getDao().deleteById(id);
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
        Metamodel metamodel = getEntityManager().getMetamodel();
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

    @Transactional(readOnly = true)
    public E findById(Class<E> clz, Object id)
    {
        return getEntityManager().find(clz, id);
    }

    public Iterable<E> findAll()
    {
        return getDao().findAll();
    }

    public Iterable<E> findAll(Specification<E> spec)
    {
        return getDao().findAll(spec);
    }

    protected EntityManager getEntityManager()
    {
        return entityManager;
    }

    @Transactional(readOnly = true)
    public Page<E> load(Pageable pageable, Class<E> clz, JPARestriction restriction)
    {
        return load(pageable, clz, restriction, null);
    }

    @Transactional(readOnly = true)
    public Page<E> load(Pageable pageable, Class<E> clz, JPARestriction restriction, Sort sort)
    {      
        List<E> result = new ArrayList<>();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteria = builder.createQuery( clz);
        Root<E> root = criteria.from( clz );
        criteria.select( root );
        if (restriction != null)
        {
            List<Predicate> predicates = restriction.applyFilter(builder, criteria, root);
            if(CollectionUtil.isNotEmpty(predicates))
            criteria.where(predicates.toArray(new Predicate[predicates.size()]));
        }
        if (sort != null)
        {
            List<javax.persistence.criteria.Order> orderList = new ArrayList<>();
            sort.forEach(s -> {
                if (StringUtil.isNotEmpty(s.getProperty()))
                {
                    if (s.isAscending())
                        orderList.add(builder.asc(root.get(s.getProperty())));
                    else
                        orderList.add(builder.desc(root.get(s.getProperty())));
                }
            });
        }
        
        TypedQuery<E> createQuery = entityManager.createQuery( criteria );        
        createQuery.setFirstResult((int)pageable.getOffset());
        // set 0 for unlimited
        if (pageable.getPageSize() > 0)
            createQuery.setMaxResults(pageable.getPageSize());
        result = createQuery.getResultList();
        return PageableExecutionUtils.getPage(result, pageable, () -> {
            return count(clz, restriction);
        });
    }
    
    
    @Transactional(readOnly = true)
    public Long count(Class<E> clz, JPARestriction restriction)
    {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery( Long.class);
        Root<E> root = criteria.from( clz );
        criteria.select(builder.count(root));
        if (restriction != null)
            restriction.applyFilter(builder, criteria, root);        
        return (Long) entityManager.createQuery(criteria).getSingleResult();
    }

}
