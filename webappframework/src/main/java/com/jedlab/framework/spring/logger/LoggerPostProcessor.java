package com.jedlab.framework.spring.logger;

import java.lang.reflect.Field;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class LoggerPostProcessor implements BeanPostProcessor
{

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException
    {
        ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException
            {
                if (field.getAnnotation(Logger.class) != null)
                {
                    ReflectionUtils.makeAccessible(field);
                    org.slf4j.Logger log = LoggerFactory.getLogger(bean.getClass());
                    field.set(bean, log);
                }
            }
        });
        return bean;
    }

}

