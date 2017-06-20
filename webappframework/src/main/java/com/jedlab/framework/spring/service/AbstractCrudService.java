package com.jedlab.framework.spring.service;

import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import com.jedlab.framework.spring.dao.AbstractCrudDAO;

/**
 * @author Omid Pourhadi
 *
 */
@Transactional
public abstract class AbstractCrudService<E> extends AbstractService<E>
{

    @Transactional
    public void deleteSoft(Long id)
    {
        ((AbstractCrudDAO)getDao()).deleteSoft(id, new Date());
    }
    
}
