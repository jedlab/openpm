package com.jedlab.framework.mapper;

import java.lang.reflect.ParameterizedType;

import org.modelmapper.PropertyMap;

public abstract class DTOPropertyMapper<S, D> extends PropertyMap<S, D>
{
    
    
    
    public Class<S> getSourceType()
    {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<S>) parameterizedType.getActualTypeArguments()[0];
    }
    
    
    public Class<D> getDestinationType()
    {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<D>) parameterizedType.getActualTypeArguments()[1];
    }
    
    
    
}
