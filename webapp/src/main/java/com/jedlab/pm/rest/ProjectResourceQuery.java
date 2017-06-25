package com.jedlab.pm.rest;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.jedlab.framework.exceptions.ServiceException;
import com.jedlab.framework.spring.rest.RestWriter;
import com.jedlab.framework.spring.rest.TypeUtil;
import com.jedlab.framework.spring.security.AuthenticationUtil;
import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.spring.service.Restriction;
import com.jedlab.framework.util.CollectionUtil;
import com.jedlab.pm.model.Project;
import com.jedlab.pm.security.AuthSuccessHandler;
import com.jedlab.pm.service.ProjectService;

/**
 * @author omidp
 *
 */
@Component
@Path("/projects")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProjectResourceQuery extends ResourceQuery<Project>
{

    @Autowired
    ProjectService projectService;
    
    @Autowired
    ProjectEntityResourceQuery subResourceLocator;

    
    @Override
    protected AbstractCrudService<Project> getService()
    {
        return projectService;
    }

    @Override
    public EntityResourceQuery<Project> getEntityResourceQuery()
    {
        return subResourceLocator;
    }
    
    @Override
    protected Restriction getRestriction()
    {
        return criteria ->
        {
            criteria.createAlias("owner", "o", JoinType.LEFT_OUTER_JOIN);
            if(AuthenticationUtil.getUserId() != null)
                criteria.add(Restrictions.eq("o.id", AuthenticationUtil.getUserId()));
        };
    }

}
