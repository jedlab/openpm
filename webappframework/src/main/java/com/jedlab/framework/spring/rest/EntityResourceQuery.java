package com.jedlab.framework.spring.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


import com.jedlab.framework.exceptions.ServiceException;
import com.jedlab.framework.spring.dao.BasePO;
import com.jedlab.framework.spring.service.AbstractCrudService;

public class EntityResourceQuery<T extends BasePO>
{

    @Context
    private UriInfo uriInfo;

    private T entity;
    private AbstractCrudService<T> service;

    public T getEntity()
    {
        return entity;
    }

    public void setEntity(T entity)
    {
        this.entity = entity;
    }

    public AbstractCrudService<T> getService()
    {
        return service;
    }

    public void setService(AbstractCrudService<T> service)
    {
        this.service = service;
    }

    @DELETE
    @RestWriter
    public Response delete() throws ServiceException
    {
        if (getEntity() == null)
        {
            throw new WebApplicationException();
        }
        T entity = getEntity();
        getService().deleteSoft(getEntity().getId());        
        return Response.ok().build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RestWriter
    public Response get()
    {
        if (getEntity() == null)
        {
            throw new WebApplicationException();
        }

        GenericEntity<T> genericEntity = new GenericEntity<T>(entity, entity.getClass());
        return Response.ok(genericEntity).build();
    }

}
