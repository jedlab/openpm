package com.jedlab.framework.mapper;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ModelMapper implements InitializingBean, ApplicationContextAware
{

    ApplicationContext applicationContext;

    private final Map<TupleKey<?, ?>, PropertyMap<?, ?>> mappings = new TreeMap<TupleKey<?, ?>, PropertyMap<?, ?>>();

    public <S, D> void addMappings(DTOPropertyMapper<S, D> propMap)
    {
        mappings.put(new TupleKey<S, D>(propMap.getSourceType().getName() + propMap.getDestinationType().getName(), propMap.getSourceType(),
                propMap.getDestinationType()), propMap.getPropertyMap());
    }

    public <S, D> PropertyMap<S, D> getTypeMap(Class<S> src, Class<D> dest)
    {
        PropertyMap<S, D> propertyMap = (PropertyMap<S, D>) mappings.get(new TupleKey<S, D>(src.getName() + dest.getName(), src, dest));
        if(propertyMap == null)
            throw new ModelMapperException("no mapper found");
        return propertyMap;
    }

    public static class TupleKey<S, D> implements java.lang.Comparable<TupleKey<S, D>>
    {
        private String name;
        private Class<S> src;
        private Class<D> des;

        public TupleKey(String name, Class<S> src, Class<D> des)
        {
            this.name = name;
            this.src = src;
            this.des = des;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TupleKey other = (TupleKey) obj;
            if (name == null)
            {
                if (other.name != null)
                    return false;
            }
            else if (!name.equals(other.name))
                return false;
            return true;
        }

        @Override
        public int compareTo(TupleKey<S, D> o)
        {
            return this.name.compareTo(o.name);
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;

    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        for (Map.Entry<String, DTOPropertyMapper> entry : BeanFactoryUtils
                .beansOfTypeIncludingAncestors(applicationContext, DTOPropertyMapper.class).entrySet())
        {
            addMappings(entry.getValue());
        }

    }

}
