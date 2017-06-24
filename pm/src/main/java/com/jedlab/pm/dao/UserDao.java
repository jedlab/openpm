package com.jedlab.pm.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jedlab.framework.spring.dao.AbstractDAO;
import com.jedlab.pm.model.User;

public interface UserDao extends AbstractDAO<User>
{

    @Modifying
    @Query("update User u set u.enabled = true where u.id = :uid")
    public void activateUserById(@Param("uid") Long userId); 
    
}
