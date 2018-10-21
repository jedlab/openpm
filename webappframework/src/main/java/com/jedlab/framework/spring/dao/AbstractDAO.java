package com.jedlab.framework.spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author omidp
 *
 * @param <T>
 */
@NoRepositoryBean
public interface AbstractDAO<T> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T>
{

    
}
