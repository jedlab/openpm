package com.jedlab.framework.spring.service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author Omid Pourhadi
 *
 */
public interface Restriction
{

    public void applyFilter(CriteriaBuilder builder, CriteriaQuery criteria, Root root);

    
    
}
