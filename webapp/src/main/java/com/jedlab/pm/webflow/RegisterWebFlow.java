package com.jedlab.pm.webflow;

import java.io.Serializable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.jedlab.framework.mail.MailClient;
import com.jedlab.framework.spring.web.SpringViewScope;
import com.jedlab.pm.model.User;
import com.jedlab.pm.service.UserService;

public class RegisterWebFlow implements Serializable
{

    @Autowired
    transient UserService userService;

    @Autowired
    transient MailClient mailClient;

    private User userModel = new User();

    public User getUserModel()
    {
        return userModel;
    }

    public String resgisterUser()
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwd = encoder.encode(userModel.getPasswd());
        userModel.setPasswd(passwd);
        userService.save(userModel);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("username", userModel.getUsername());
        maps.put("code", userModel.getId());
        mailClient.send("info@jedlab.ir", userModel.getEmail(), "Verify Account", "/com/jedlab/pm/controller/register.vm", maps,
                new UncaughtExceptionHandler() {

                    public void uncaughtException(Thread t, Throwable e)
                    {
                        System.out.println("email is not sent ");
                    }
                });
        
        return "thankyou";
    }

}
