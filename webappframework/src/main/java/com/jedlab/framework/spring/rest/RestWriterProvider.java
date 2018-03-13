package com.jedlab.framework.spring.rest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.omidbiz.core.axon.Axon;
import org.omidbiz.core.axon.AxonBuilder;
import org.omidbiz.core.axon.Filter;
import org.omidbiz.core.axon.Property;
import org.omidbiz.core.axon.internal.SerializationContext;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.jedlab.framework.reflections.ReflectionUtil;
import com.jedlab.framework.spring.rest.RestWriter;

/**
 * @author omidp
 *
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON })
public class RestWriterProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object>
{

    private static final Axon axon = new AxonBuilder().preventRecursion().useWithPrettyWriter().addFilter(new JsonFilter()).create();

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

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        for (Annotation annotation : annotations)
        {
            if (annotation.annotationType().equals(RestReader.class))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
    {
        Class<?> entityClass = null;
        if (genericType instanceof java.lang.reflect.ParameterizedType)
        {
            entityClass = (Class<?>) ((java.lang.reflect.ParameterizedType) genericType).getActualTypeArguments()[0];

        }
        else
        {
            entityClass = type;
        }

        byte[] bs = ByteStreams.toByteArray(entityStream);
        InputStream beanInputStream = new ByteArrayInputStream(bs);
        InputStreamReader isr = new InputStreamReader(beanInputStream, "UTF-8");
        String jsonContent = CharStreams.toString(isr);
        IOUtils.closeQuietly(isr);
        IOUtils.closeQuietly(beanInputStream);
        //Axon axon = new AxonBuilder().addTypeConverter(new EpochDateConverter()).create();
        Object bean = axon.toObject(jsonContent, entityClass, null);
        return bean;
    }
    
    

}
