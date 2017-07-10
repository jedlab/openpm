package com.jedlab.pm.model;

import com.jedlab.framework.spring.security.SecurityUser;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_name", "email" }) })
public class User extends SecurityUser
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
