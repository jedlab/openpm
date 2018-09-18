package com.jedlab.pm;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Initializer implements ServletContextInitializer
{

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException
    {
        System.err.println("------------------------------------");
        servletContext.setInitParameter("primefaces.CLIENT_SIDE_VALIDATION", "true");
//        servletContext.setInitParameter("org.ocpsoft.rewrite.annotation.SCAN_LIB_DIRECTORY", "true");
        servletContext.setInitParameter("org.ocpsoft.rewrite.annotation.BASE_PACKAGES", "com.jedlab.pm.web.action");
        servletContext.setInitParameter("com.sun.faces.forceLoadConfiguration", Boolean.TRUE.toString());
        servletContext.addListener(com.sun.faces.config.ConfigureListener.class);
        servletContext.setInitParameter("javax.faces.FACELETS_LIBRARIES", "/WEB-INF/springsecurity.taglib.xml");
        
        servletContext.setInitParameter("primefaces.THEME", "none");
        servletContext.setInitParameter("primefaces.FONT_AWESOME", "true");
        servletContext.setInitParameter("primefaces.DIR", "rtl");
    }

}