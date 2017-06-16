package com.jedlab.framework.spring.dao;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author omidp
 *
 * @param <T>
 */
@NoRepositoryBean
public interface AbstractDAO<T> extends PagingAndSortingRepository<T, Long>, JpaSpecificationExecutor<T>
{

    
}
