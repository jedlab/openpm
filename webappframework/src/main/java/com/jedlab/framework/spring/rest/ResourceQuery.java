package com.jedlab.framework.spring.rest;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;

import com.google.common.collect.Lists;
import com.jedlab.framework.exceptions.ServiceException;
import com.jedlab.framework.spring.dao.BasePO;
import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.spring.service.JPARestriction;
import com.jedlab.framework.util.CollectionUtil;

public abstract class ResourceQuery<T extends BasePO> 
{
    
    @Autowired
    MessageSource messageSource;

    @Context
    protected UriInfo uriInfo;

    public final Class<T> getEntityClass()
    {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    abstract protected AbstractCrudService<T> getService();
    
    @GET
    @Produces("application/json")
    @RestWriter
    public Response search(@Context HttpServletRequest request,@QueryParam("start") @DefaultValue("0") int start, 
            @QueryParam("show") @DefaultValue("25") int show)
            throws ServiceException, UnsupportedEncodingException
    {
        if ((start < 0) || (show < 0))
        {
           return Response.status(Status.BAD_REQUEST).build();
        }
        
        Iterable<T> projects = getService().load(PageRequest.of(start, show), getEntityClass(), getRestriction());
        ArrayList<T> projectList = Lists.newArrayList(projects);
        if (CollectionUtil.isNotEmpty(projectList))
        {
            java.lang.reflect.Type responseType = new TypeUtil(projectList, getEntityClass());
            GenericEntity<List<T>> entityWrapperList = new GenericEntity<List<T>>(projectList, responseType);
            return Response.ok(entityWrapperList).build();
        }
        return Response.noContent().build();
    }
    
    protected JPARestriction getRestriction()
    {
        return null;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RestWriter
    public Response insert(@RestReader T entity) throws ServiceException
    {
        getService().insert(entity);
        // Redirect after create
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(entity.getId())).build();
        T find = find(entity.getId());        
        return Response.created(uri).entity(find).build();
    }
    
    protected T find(Long entityId)
    {
        return getService().findById(getEntityClass(), entityId);
    }
    
    public abstract EntityResourceQuery<T> getEntityResourceQuery();
    
    @Path("{id : \\d+}")
    public final EntityResourceQuery<T> getEntityResource(@PathParam("id") Long id) throws ServiceException
    {
        EntityResourceQuery<T> entityResource = getEntityResourceQuery();
        if (entityResource == null)
        {
            throw new ServiceException("no subresource locator");
        }
        T entityModel = find(id);
        if (entityModel == null)
        {
            throw new ServiceException(messageSource.getMessage("loadSubResourceLocator", null, Locale.getDefault()));
        }
        entityResource.setEntity(entityModel);
        entityResource.setService(getService());
        return entityResource;
    }
    
}
