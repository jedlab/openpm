package com.jedlab.pm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.jedlab.pm.model.User;

public class UserDetailService implements UserDetailsService
{

    @Autowired
    UserService userService;
    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userService.findByUsername(username);
        if(user == null)
            throw new UsernameNotFoundException(username);
        return user;
    }

}
