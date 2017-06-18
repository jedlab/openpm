package com.omidp.oframework.test.model;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.jedlab.framework.spring.security.SecurityUser;

public class User extends SecurityUser
{

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {        
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Transient
    public String getFullName()
    {
        return getUsername();
    }

}
