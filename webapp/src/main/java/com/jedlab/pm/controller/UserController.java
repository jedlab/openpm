package com.jedlab.pm.controller;

import java.io.Serializable;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.jedlab.framework.spring.web.SpringViewScope;
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
