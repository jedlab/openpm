package com.jedlab.framework.spring.rest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author omidp
 *
 */
public class TypeUtil implements ParameterizedType
{

    List<?> entities;
    Class<?> clz;

    public TypeUtil(List<?> entities, Class<?> clz)
    {
        super();
        this.entities = entities;
        this.clz = clz;
    }

    public Type getType()
    {
        return this;
    }

    @Override
    public Type[] getActualTypeArguments()
    {
        return new Type[] { clz };
    }

    @Override
    public Type getRawType()
    {
        return entities.getClass();
    }

    @Override
    public Type getOwnerType()
    {
        return null;
    }

}
