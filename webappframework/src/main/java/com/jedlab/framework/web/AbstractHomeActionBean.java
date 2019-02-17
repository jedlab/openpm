package com.jedlab.framework.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.spring.validation.BindingErrorMessage;
import com.jedlab.framework.spring.validation.ValidationUtil;

public abstract class AbstractHomeActionBean<E> extends AbstractActionBean
{

    private static final Logger logger = Logger.getLogger(AbstractHomeActionBean.class.getName());

    private Class<E> entityClass;
    protected E instance;
    private Object id;

    @Autowired
    protected transient MessageSource messageSource;

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
        getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", createdMessage()));
        getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    protected String createdMessage()
    {
        String code = getMessageKeyPrefix() + "created";
        return messageSource.getMessage(code, null, getCurrentLocale());
    }
    
    protected String updatedMessage()
    {
        String code = getMessageKeyPrefix() + "updated";
        return messageSource.getMessage(code, null, getCurrentLocale());
    }
    
    protected String deletedMessage()
    {
        String code = getMessageKeyPrefix() + "deleted";
        return messageSource.getMessage(code, null, getCurrentLocale());
    }

    protected String getMessageKeyPrefix()
    {
        String className = getEntityClass().getName();
        return className.substring(className.lastIndexOf('.') + 1) + '_';
    }

    public void update()
    {
        getService().update(getInstance());
        getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", updatedMessage()));
        getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    public void delete()
    {
        getService().deleteSoft((Long) getId());
        getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", deletedMessage()));
        getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
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
    
    
    
    /**
     * @return true if everything is ok
     */
    protected boolean isValid()
    {
        if(getValidator() == null)
            return true;
        ValidationUtil vu = new ValidationUtil(messageSource);
        BindingErrorMessage errorMsg = vu.invokeValidator(getValidator(), getInstance());
        errorMsg.getErrors().forEach(item->{
            getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "", item.getMessage()));
            getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        });
        
        return errorMsg.getErrors().size() == 0;
    }
    
    
    /**
     * @return true if everything is ok
     */
    protected boolean isValid(Validator vld, Object model)
    {
        if(vld == null || model == null)
            throw new IllegalArgumentException("vld/model can not be null");
        ValidationUtil vu = new ValidationUtil(messageSource);
        BindingErrorMessage errorMsg = vu.invokeValidator(vld, model);
        errorMsg.getErrors().forEach(item->{
            getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "", item.getMessage()));
            getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        });
        
        return errorMsg.getErrors().size() == 0;
    }
    
    protected Validator getValidator(){return null;}

}
