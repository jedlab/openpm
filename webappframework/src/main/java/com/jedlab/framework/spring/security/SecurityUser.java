package com.jedlab.framework.spring.security;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.springframework.security.core.userdetails.UserDetails;

import com.jedlab.framework.db.BasePO;

@MappedSuperclass
public abstract class SecurityUser extends BasePO implements UserDetails
{

    private String password;

    private String username;

    private boolean enabled;
    

    public SecurityUser()
    {
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    @Column(name="passwd")
    public String getPassword()
    {
        return password;
    }

    @Override
    @Column(name="user_name")
    public String getUsername()
    {
        return username;
    }

    @Override
    @Transient
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    @Column(name="is_activate")
    public boolean isEnabled()
    {
        return enabled;
    }


}
