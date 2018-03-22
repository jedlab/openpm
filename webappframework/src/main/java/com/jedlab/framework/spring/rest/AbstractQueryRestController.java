package com.jedlab.framework.spring.rest;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jedlab.framework.db.EntityModel;
import com.jedlab.framework.spring.mvc.Pager;
import com.jedlab.framework.spring.rest.QueryWhereParser.FilterProperty;
import com.jedlab.framework.spring.service.AbstractService;
import com.jedlab.framework.spring.service.JPARestriction;

/**
 * @author omidp
 *
 * @param <E>
 */
public abstract class AbstractQueryRestController<E extends EntityModel>
{

    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;
    private static final int BUTTONS_TO_SHOW = 5;
    private static final int[] PAGE_SIZES = { 5, 10, 20 };

    @Autowired
    protected Validator validator;

    @Autowired
    protected MessageSource messageSource;

    @ResponseBody
    @GetMapping(value="/",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultList<E> get(@RequestParam("pageSize") Optional<Integer> pageSize, @RequestParam("page") Optional<Integer> page,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "match", required = false, defaultValue = QueryWhereParser.AND) String match, Sort sort)
            throws BindingValidationError, UnsupportedEncodingException
    {
        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;

        //
        QueryWhereParser qb = filter != null ? new QueryWhereParser(URLDecoder.decode(filter, "UTF-8")) : QueryWhereParser.EMPTY;
        if (qb != null)
            qb.setMatch(match);

        Page<E> list = getService().load(PageRequest.of(evalPage, evalPageSize), getEntityClass(), getRestriction(qb.getFilterProperties()),
                sort);
        Pager pager = new Pager(list.getTotalPages(), list.getNumber(), BUTTONS_TO_SHOW);
        return new ResultList<E>(evalPageSize, new ArrayList<>(list.getContent()), pager.getStartPage(), pager.getEndPage(),
                getService().count(getEntityClass(), getRestriction(qb.getFilterProperties())), list.getTotalPages());
    }

    protected JPARestriction getRestriction(List<FilterProperty> filterProperties)
    {
        return null;
    }

    public static class ResultList<E> implements Serializable
    {
        private int selectedPageSize;
        private ArrayList<E> resultList;
        private int startPage;
        private int endPage;
        private long resultCount;
        private int totalPage;

        public ResultList()
        {
        }

        public ResultList(int selectedPageSize, ArrayList<E> resultList, int startPage, int endPage, long resultCount, int totalPage)
        {
            this.selectedPageSize = selectedPageSize;
            this.resultList = resultList;
            this.startPage = startPage;
            this.endPage = endPage;
            this.resultCount = resultCount;
            this.totalPage = totalPage;
        }

        public int getTotalPage()
        {
            return totalPage;
        }

        public long getResultCount()
        {
            return resultCount;
        }

        public int getSelectedPageSize()
        {
            return selectedPageSize;
        }

        public void setSelectedPageSize(int selectedPageSize)
        {
            this.selectedPageSize = selectedPageSize;
        }

        public ArrayList<E> getResultList()
        {
            return resultList;
        }

        public void setResultList(ArrayList<E> resultList)
        {
            this.resultList = resultList;
        }

        public int getStartPage()
        {
            return startPage;
        }

        public void setStartPage(int startPage)
        {
            this.startPage = startPage;
        }

        public int getEndPage()
        {
            return endPage;
        }

        public void setEndPage(int endPage)
        {
            this.endPage = endPage;
        }

    }

    protected abstract AbstractService<E> getService();

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

}
