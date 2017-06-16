package com.jedlab.framework.spring.web;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.jedlab.framework.db.QueryMapper;

public abstract class DefaultFilteSpec<E> implements Specification<E>
{

    Filter filter;

    public DefaultFilteSpec(Filter filter)
    {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb)
    {
        Map<String, Object> filterMapParams = QueryMapper.filterMap(filter);
        return createCriteria(root, query, cb, filterMapParams);
    }

    protected abstract Predicate createCriteria(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb,
            Map<String, Object> filterMapParams);

}