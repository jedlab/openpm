package com.jedlab.pm.model;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.jedlab.framework.spring.security.SecurityUser;
import com.jedlab.framework.spring.security.SecurityUserContext;

@Entity
@Table(name = "users", schema = "public", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_name", "email" }) })
public class User extends SecurityUser implements SecurityUserContext
{

    private String email;

    @Column(name = "email")
    @Email
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

}
