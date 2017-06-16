package com.jedlab.framework.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.transaction.annotation.Transactional;

import com.jedlab.framework.spring.service.AbstractCrudService;

public abstract class AbstractHomeActionBean<E> extends AbstractActionBean
{

    private static final Logger logger = Logger.getLogger(AbstractHomeActionBean.class.getName());

    private Class<E> entityClass;
    protected E instance;
    private Object id;

    protected E createInstance()
    {

        if (getEntityClass() != null)
        {
            try
            {
                return getEntityClass().newInstance();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            return null;
        }
    }

    public void clearInstance()
    {
        instance = null;
        id = null;
    }

    @Transactional
    public E getInstance()
    {
        if (instance == null)
        {
            initInstance();
        }
        return instance;
    }

    /**
     * Load the instance if the id is defined otherwise create a new instance <br />
     * Utility method called by {@link #getInstance()} to load the instance from
     * the Persistence Context if the id is defined. Otherwise a new instance is
     * created.
     * 
     * @see #find()
     * @see #createInstance()
     */
    protected void initInstance()
    {
        if (isIdDefined())
        {

            setInstance(find());

        }
        else
        {
            setInstance(createInstance());
        }
    }

    public boolean isIdDefined()
    {
        return getId() != null && !"".equals(getId());
    }

    /**
     * Get the id of the object being managed.
     */
    public Object getId()
    {
        return id;
    }

    /**
     * Set/change the entity being managed by id.
     * 
     * @see #assignId(Object)
     */
    public void setId(Object id)
    {
        this.id = id;
    }

    public void setInstance(E instance)
    {

        this.instance = instance;
    }

    /**
     * Get the class of the entity being managed. <br />
     * If not explicitly specified, the generic type of implementation is used.
     */
    public Class<E> getEntityClass()
    {
        if (entityClass == null)
        {
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType)
            {
                ParameterizedType paramType = (ParameterizedType) type;
                if (paramType.getActualTypeArguments().length == 2)
                {
                    // likely dealing with -> new
                    // EntityHome<Person>().getEntityClass()
                    if (paramType.getActualTypeArguments()[1] instanceof TypeVariable)
                    {
                        throw new IllegalArgumentException("Could not guess entity class by reflection");
                    }
                    // likely dealing with -> new Home<EntityManager, Person>()
                    // { ... }.getEntityClass()
                    else
                    {
                        entityClass = (Class<E>) paramType.getActualTypeArguments()[1];
                    }
                }
                else
                {
                    // likely dealing with -> new PersonHome().getEntityClass()
                    // where PersonHome extends EntityHome<Person>
                    entityClass = (Class<E>) paramType.getActualTypeArguments()[0];
                }
            }
            else
            {
                throw new IllegalArgumentException("Could not guess entity class by reflection");
            }
        }
        return entityClass;
    }

    public abstract AbstractCrudService<E> getService();


    public void save()
    {
        getService().insert(getInstance());
    }

    public void update()
    {
        getService().update(getInstance());
    }

    public void delete()
    {
        getService().deleteSoft((Long)getId());
    }

    public void load()
    {
        logger.info("load");
    }

    @PostConstruct
    public void init()
    {
        logger.info("init");
    }

    protected E find()
    {
        return getService().findById(getEntityClass(), getId());
    }

}
