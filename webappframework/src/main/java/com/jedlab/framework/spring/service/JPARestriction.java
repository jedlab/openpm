package com.jedlab.framework.spring.service;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@FunctionalInterface
public interface JPARestriction
{
    
    @SuppressWarnings("rawtypes")
    public List<Predicate> applyFilter(CriteriaBuilder builder, CriteriaQuery criteria, Root root);

}
