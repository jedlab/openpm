package com.jedlab.pm.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jedlab.pm.model.User;

public interface UserDao extends CrudRepository<User, Long>, JpaSpecificationExecutor<User>
{

    @Modifying
    @Query("update User u set u.activated = true where u.id = :uid")
    public void activateUserById(@Param("uid") Long userId); 
    
}
