package com.jedlab.pm.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.omidbiz.core.axon.Axon;
import org.omidbiz.core.axon.AxonBuilder;
import org.omidbiz.core.axon.Filter;
import org.omidbiz.core.axon.Property;
import org.omidbiz.core.axon.filters.RecursionControlFilter;
import org.omidbiz.core.axon.internal.BasicElement;
import org.omidbiz.core.axon.internal.Element;
import org.omidbiz.core.axon.internal.ObjectElement;
import org.omidbiz.core.axon.internal.SerializationContext;
import org.omidbiz.core.axon.internal.TypeConverter;

import com.jedlab.framework.reflections.ReflectionUtil;
import com.jedlab.framework.spring.rest.RestWriter;
import com.jedlab.framework.util.HibernateUtil;
import com.jedlab.pm.model.User;

/**
 * @author omidp
 *
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON })
public class RestWriterProvider implements MessageBodyWriter<Object>
{

    private static final Axon axon = new AxonBuilder().preventRecursion().useWithPrettyWriter().addTypeConverter(new JsonUserTypeConverter()).addFilter(new JsonFilter()).create();

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        for (Annotation annotation : annotations)
        {
            if (annotation.annotationType().equals(RestWriter.class))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
    {

        Class<?> entityClass = t.getClass();
        if (ReflectionUtil.isCollection(t))
        {
            entityClass = (Class<?>) ((java.lang.reflect.ParameterizedType) genericType).getActualTypeArguments()[0];
        }
        String json = axon.toJson(t);
        entityStream.write(json.getBytes("UTF-8"));
        IOUtils.closeQuietly(entityStream);

    }
    
    public static class JsonUserTypeConverter implements TypeConverter<User>
    {

        @Override
        public boolean applies(Object instance)
        {
            return instance.getClass().equals(User.class);
        }

        @Override
        public Element write(User instance, SerializationContext ctx)
        {
            return new BasicElement(instance.getId());
        }
        
    }
    
    public static class JsonFilter implements Filter
    {

        @Override
        public void beforeFilter(SerializationContext ctx)
        {
            
        }

        @Override
        public boolean exclude(String path, Object target, Property property, Object propertyValue)
        {
            if(property == null)
                return false;
            return "password".equals(property.getName());
        }

        @Override
        public void afterFilter()
        {
            
        }
        
    }

}
