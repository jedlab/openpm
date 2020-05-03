package com.jedlab.framework.mapper;

import java.lang.reflect.ParameterizedType;

public abstract class DTOPropertyMapper<S, D>
{

    public abstract PropertyMap<S, D> getPropertyMap();

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
