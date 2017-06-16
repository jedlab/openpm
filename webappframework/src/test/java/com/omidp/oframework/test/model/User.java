package com.omidp.oframework.test.model;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.jedlab.framework.db.EntityModel;

public class User implements EntityModel<Long>
{

    private Long id;

    private String name;

    private String userName;

    @Transient
    public String getFullName()
    {
        return getUserName() + " " + getName();
    }

    @Column(name = "user_name")
    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Column(name = "name")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Column(name = "id")
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

}
