package com.jedlab.framework.spring.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.transaction.annotation.Transactional;

import com.jedlab.framework.db.EntityModel;
import com.jedlab.framework.exceptions.ServiceException;
import com.jedlab.framework.spring.dao.AbstractCrudDAO;
import com.jedlab.framework.spring.dao.AbstractDAO;
import com.jedlab.framework.util.CollectionUtil;
import com.jedlab.framework.web.ExtendedLazyDataModel.SortProperty;

/**
 * @author Omid Pourhadi
 *
 */
@Transactional
public abstract class AbstractCrudService<E> extends AbstractService<E>
{

    
    public void deleteSoft(Long id)
    {
        ((AbstractCrudDAO)getDao()).deleteSoft(id, new Date());
    }
    
}
