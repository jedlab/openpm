package com.jedlab.pm.rest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.jedlab.framework.exceptions.ServiceException;
import com.jedlab.framework.spring.rest.RestWriter;
import com.jedlab.framework.spring.rest.TypeUtil;
import com.jedlab.framework.util.CollectionUtil;
import com.jedlab.pm.model.Project;
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
    public Response search(@Context HttpServletRequest request) throws ServiceException, UnsupportedEncodingException
    {
        Iterable<Project> projects = projectService.findAll();
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
