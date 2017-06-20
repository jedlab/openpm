package com.jedlab.pm.dataaccess.webflow;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author omidp
 *
 */
public class HibernateSessionFactoryBuilder implements ApplicationContextAware
{

    ApplicationContext applicationContext;
    EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory)
    {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public SessionFactory createSessionFactory()
    {
        HibernateEntityManagerFactory hemf = (HibernateEntityManagerFactory) entityManagerFactory
                .unwrap(org.hibernate.jpa.HibernateEntityManagerFactory.class);
        return hemf.getSessionFactory();
    }
}
