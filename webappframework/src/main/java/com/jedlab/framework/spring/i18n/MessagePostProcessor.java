package com.jedlab.framework.spring.i18n;

import java.lang.reflect.Field;
import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.jedlab.framework.util.StringUtil;

/**
 * @author Omid Pourhadi
 *
 */
public class MessagePostProcessor implements BeanPostProcessor, MessageSourceAware
{

    MessageSource messageSource;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException
            {
                com.jedlab.framework.spring.i18n.MessageSource msgAnnotation = field
                        .getAnnotation(com.jedlab.framework.spring.i18n.MessageSource.class);
                if (msgAnnotation != null)
                {
                    ReflectionUtils.makeAccessible(field);
                    String code = msgAnnotation.code();
                    if (StringUtil.isEmpty(code))
                        code = field.getName();
                    String message = messageSource.getMessage(code, null, new Locale(msgAnnotation.locale()));
                    field.set(bean, message);
                }
            }
        });
        return bean;
    }

    @Override
    public void setMessageSource(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

}
