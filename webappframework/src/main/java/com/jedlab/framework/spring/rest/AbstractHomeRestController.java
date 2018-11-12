package com.jedlab.framework.spring.rest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jedlab.framework.db.EntityModel;
import com.jedlab.framework.spring.SpringUtil;
import com.jedlab.framework.spring.mvc.EntityWrapper;
import com.jedlab.framework.spring.service.AbstractService;
import com.jedlab.framework.spring.service.Restriction;
import com.jedlab.framework.spring.validation.BindingErrorMessage;
import com.jedlab.framework.spring.validation.BindingValidationError;
import com.jedlab.framework.spring.validation.ValidationUtil;
import com.jedlab.framework.util.CollectionUtil;
import com.jedlab.framework.util.StringUtil;

/**
 * @author omidp
 *
 * @param <E>
 */
public abstract class AbstractHomeRestController<E extends EntityModel>
{

    @Autowired
    protected Validator validator;

    @Autowired
    protected MessageSource messageSource;

    @ResponseBody
    @PostMapping(value="/",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> post(@RequestBody E entity, Errors errors, HttpServletRequest request) throws BindingValidationError
    {
        createInstance(entity, request);
        validate(entity, errors);
        getService().insert(entity);
        return ResponseEntity.ok(new ResponseMessage(SpringUtil.getMessage("successful", null), 0));
    }
    
    
    @ResponseBody
    @PutMapping(value="/{id}",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> put(@RequestBody EntityWrapper<E> entity, @PathVariable("id") Long id, Errors errors, HttpServletRequest request) throws BindingValidationError
    {
        createInstance(entity.getPersistedEntity(), request);
        validate(entity.getPersistedEntity(), errors);
        getService().update(entity.getPersistedEntity());
        return ResponseEntity.ok(new ResponseMessage(SpringUtil.getMessage("successful", null), 0));
    }
    
    
    @ResponseBody
    @DeleteMapping(value="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> delete(@PathVariable("id") Long id, Errors errors) throws BindingValidationError
    {
        getService().delete(id);
        return ResponseEntity.ok(new ResponseMessage(SpringUtil.getMessage("successful", null), 0));
    }

    
    protected Restriction getRestriction()
    {
        return null;
    }
    
    protected void createInstance(E entity, HttpServletRequest request)
    {
        
    }

    protected abstract AbstractService<E> getService();

    private void validate(E validated, Errors errors) throws BindingValidationError
    {
        // BeanPropertyBindingResult bindingResult = new
        // BeanPropertyBindingResult(validated, Person.class.getSimpleName());
        // spring validator
        validator.validate(validated, errors);        
        if (getValidator() != null && getValidator().supports(getEntityClass()))
            getValidator().validate(validated, errors);
        if (errors.hasErrors())
        {
            throw new BindingValidationError(errors.getAllErrors());
        }
    }

    protected Validator getValidator()
    {
        return null;
    }
    
    private Class<E> entityClass;
    
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
                    if (paramType.getActualTypeArguments()[1] instanceof TypeVariable)
                    {
                        throw new IllegalArgumentException("Could not guess entity class by reflection");
                    }
                    else
                    {
                        entityClass = (Class<E>) paramType.getActualTypeArguments()[1];
                    }
                }
                else
                {
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


    @ExceptionHandler(BindingValidationError.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BindingErrorMessage handleFormValidationError(BindingValidationError validationError)
    {

        BindingErrorMessage bem = new BindingErrorMessage();
        
        List<ObjectError> errors = validationError.getErrors();
        
        ValidationUtil vu = new ValidationUtil(messageSource);
        
        vu.processFieldErrors(errors, bem);
        
        vu.processGlobalErrors(errors, bem);
        

        return bem;
    }


    

}
