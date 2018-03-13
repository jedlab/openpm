package com.jedlab.framework.spring.mvc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jedlab.framework.db.EntityModel;
import com.jedlab.framework.spring.SpringUtil;
import com.jedlab.framework.spring.service.AbstractService;
import com.jedlab.framework.util.StringUtil;

/**
 *
 * @author Omid Pourhadi
 *
 */
public abstract class AbstractHomeController<E extends EntityModel>
{

    private Class<E> entityClass;
    private String formPath;
    private String redirectListPath;

    public abstract AbstractService<E> getService();

    public AbstractHomeController(String formPath, String redirectListPath)
    {
        this.formPath = formPath;
        this.redirectListPath = redirectListPath;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        init(binder);
    }

    protected void init(WebDataBinder binder)
    {

    }

    @RequestMapping(method = RequestMethod.GET)
    public String get()
    {
        return formPath;
    }

    @ModelAttribute("instance")
    public final E instance(HttpServletRequest request)
    {
        try
        {
            String idParam = request.getParameter(editParamId());
            if (StringUtil.isNotEmpty(idParam))
            {
                request.setAttribute("idDefined", true);
                return getService().findById(getEntityClass(), Long.parseLong(idParam));
            }
            request.setAttribute("idDefined", false);
            return getEntityClass().newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new IllegalArgumentException("unable to create instance");
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String post(@Valid @ModelAttribute("instance") E entity, BindingResult bindingResult, RedirectAttributes attributes)
    {
        if (bindingResult.hasErrors())
        {
            return formPath;
        }
        if(entity.getId() != null)
        {
            String msg = SpringUtil.getMessage("entity.updated", null);
            attributes.addFlashAttribute("msg", msg);
            getService().update(entity);
        }
        else
        {
            String msg = SpringUtil.getMessage("entity.created", null);
            attributes.addFlashAttribute("msg", msg);
            getService().insert(entity);
        }
        return redirectListPath;
    }

    protected abstract String editParamId();

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

}
