package com.jedlab.framework.spring.service;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@FunctionalInterface
public interface JPARestriction
{

    /**
     * <p>for internal use only</p>
     * @param builder
     * @param criteria
     * @param root
     * @param joinFetch
     * @return
     */
    @SuppressWarnings("rawtypes")
    public void applyFilter(CriteriaBuilder builder, CriteriaQuery criteria, Root root, boolean joinFetch);

    static JPARestrictionImpl create(Restriction restriction)
    {
        return new JPARestrictionImpl(restriction);
    }

}
