package com.jedlab.framework.spring;


import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;

/**
 * SpringUtil offers statics method if you want to wire spring bean into non
 * spring bean
 * 
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [DOT] com
 * @since Spring
 * @version 0.1
 */
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public static void autoWire(Class<?> clz)
    {
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        Object bean = autowireCapableBeanFactory.createBean(clz, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
        autowireCapableBeanFactory.autowireBean(bean);
    }

    public static <T> T getBean(Class<T> clz)
    {
        return applicationContext.getAutowireCapableBeanFactory().getBean(clz);
    }

    public static boolean containsBean(String name)
    {
        return applicationContext.containsBean(name);

    }

    public static boolean isSingleton(String name)
    {
        return applicationContext.isSingleton(name);
    }
    
    public static boolean isPrototype(String name)
    {
        return applicationContext.isPrototype(name);
    }
    
    public static String getMessage(String code, Object[] args)
    {
        MessageSource bean = getBean(MessageSource.class);
        return bean.getMessage(code, args, new Locale("fa", "IR"));
    }

}
