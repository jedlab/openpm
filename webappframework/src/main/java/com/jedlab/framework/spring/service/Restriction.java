package com.jedlab.framework.spring.service;

import org.hibernate.Criteria;

/**
 * @author Omid Pourhadi
 *
 */
@Deprecated
public interface Restriction
{

    public void applyFilter(Criteria criteria);

    
    
}
