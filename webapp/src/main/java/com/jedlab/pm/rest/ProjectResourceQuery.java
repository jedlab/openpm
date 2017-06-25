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
public class ProjectResourceQuery
{

    @Autowired
    ProjectService projectService;

    @GET
    @Produces("application/json")
    @RestWriter
    public Response search(@Context HttpServletRequest request,@QueryParam("start") @DefaultValue("0") int start, 
            @QueryParam("show") @DefaultValue("25") int show)
            throws ServiceException, UnsupportedEncodingException
    {
        if ((start < 0) || (show < 0))
        {
           return Response.status(BAD_REQUEST).build();
        }
        
        Iterable<Project> projects = projectService.load(start, show, null, null, Project.class, criteria ->
        {
            criteria.createAlias("owner", "o", JoinType.LEFT_OUTER_JOIN);
            if(AuthenticationUtil.getUserId() != null)
                criteria.add(Restrictions.eq("o.id", AuthenticationUtil.getUserId()));
        });
        ArrayList<Project> projectList = Lists.newArrayList(projects);
        if (CollectionUtil.isNotEmpty(projectList))
        {
            java.lang.reflect.Type responseType = new TypeUtil(projectList, Project.class);
            GenericEntity<List<Project>> entityWrapperList = new GenericEntity<List<Project>>(projectList, responseType);
            return Response.ok(entityWrapperList).build();
        }
        return Response.noContent().build();
    }

}
