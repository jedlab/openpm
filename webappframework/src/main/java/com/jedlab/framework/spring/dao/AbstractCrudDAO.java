package com.jedlab.framework.spring.dao;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author omidp
 *
 * @param <T>
 */
@NoRepositoryBean
public interface AbstractCrudDAO<T extends PO> extends AbstractDAO<T>
{

    //implements Custom behaviour
    @Modifying
    @Query("update #{#entityName} e set e.deletedDate = ?2, e.version= (e.version+1) where e.id= ?1")
    public void deleteSoft(Serializable id, Date date);
    
}
