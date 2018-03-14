package com.jedlab.framework.spring.mvc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jedlab.framework.db.QueryMapper;
import com.jedlab.framework.spring.service.AbstractService;
import com.jedlab.framework.spring.service.JPARestriction;
import com.jedlab.framework.spring.service.Restriction;
import com.jedlab.framework.spring.web.Filter;

/**
 *
 * @author Omid Pourhadi
 *
 */
public abstract class AbstractQueryController<E>
{

    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;
    private static final int BUTTONS_TO_SHOW = 5;
    private static final int[] PAGE_SIZES = { 5, 10, 20 };

    private Class<E> entityClass;
    private String path;

    public AbstractQueryController(String path)
    {
        this.path = path;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        init(binder);
    }

    protected void init(WebDataBinder binder)
    {

    }

    @ModelAttribute("filter")
    public final Filter filter()
    {
        return createFilter();
    }

    protected Filter createFilter()
    {
        return null;
    }

    public abstract AbstractService<E> getService();

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam("pageSize") Optional<Integer> pageSize, @RequestParam("page") Optional<Integer> page, Model model,
            @ModelAttribute("filter") Filter filter, Sort sort)
    {
        // Evaluate page size. If requested parameter is null, return initial
        // page size
        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        // Evaluate page. If requested parameter is null or less than 0 (to
        // prevent exception), return initial size. Otherwise, return value of
        // param. decreased by 1.

        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
        Page<E> list = getService().load(new PageRequest(evalPage, evalPageSize), getEntityClass(), getRestriction(filter), sort);

        Pager pager = new Pager(list.getTotalPages(), list.getNumber(), BUTTONS_TO_SHOW);

        model.addAttribute("selectedPageSize", evalPageSize);
        model.addAttribute("pageSizes", PAGE_SIZES);
        model.addAttribute("pager", pager);
        model.addAttribute("result", list);
        model.addAttribute("filterParams", QueryMapper.queryParams(filter));
        model.addAttribute("sortParams", QueryMapper.sortQueryParams(sort));

        return path;
    }

   

    protected JPARestriction getRestriction(Filter filter)
    {
        return null;
    }

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
