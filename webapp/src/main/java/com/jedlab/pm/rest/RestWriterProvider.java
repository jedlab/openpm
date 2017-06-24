package com.jedlab.pm.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;

import com.jedlab.framework.reflections.ReflectionUtil;
import com.jedlab.framework.spring.rest.RestWriter;

/**
 * @author omidp
 *
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON })
public class RestWriterProvider implements MessageBodyWriter<Object>
{

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
//        entityStream.write(content.getBytes("UTF-8"));
        entityStream.write("comming soon".getBytes("UTF-8"));
        IOUtils.closeQuietly(entityStream);
        
    }

}
