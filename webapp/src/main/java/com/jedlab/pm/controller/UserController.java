package com.jedlab.pm.controller;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.jedlab.pm.model.User;
import com.jedlab.pm.service.UserService;

@Controller
public class UserController implements Serializable
{

    @Autowired
    transient UserService userService;
    
    public void activate(User u)
    {
        userService.activateUserById(u.getId());
    }
    
    public User loadUser(Long userId)
    {
        User u = userService.findById(userId);
        return u;
    }

}
