package com.jedlab.framework.spring.service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;


public interface JPARestriction
{

    
    @SuppressWarnings("rawtypes")
    public Specification countSpec(CriteriaBuilder builder, CriteriaQuery criteria, Root root);
    
    @SuppressWarnings("rawtypes")
    public Specification listSpec(CriteriaBuilder builder, CriteriaQuery criteria, Root root);

    
}
