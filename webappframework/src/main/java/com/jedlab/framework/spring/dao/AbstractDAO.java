package com.jedlab.framework.spring.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
