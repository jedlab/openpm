package com.jedlab.framework.spring.mvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.dom4j.tree.AbstractEntity;
import org.omidbiz.core.axon.Axon;
import org.omidbiz.core.axon.AxonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.jedlab.framework.spring.dao.BasePO;
import com.jedlab.framework.util.StringUtil;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [DOT] com
 * 
 *         <pre>
 *  {@code
 * &#64;RequestMapping(method = RequestMethod.POST)
 * public void post(@RequestBody ModelEntity model){
 * }
 *     
 * &#64;RequestMapping(method = RequestMethod.PUT)
 * public void put(@RequestBody EntityWrapper<ModelEntity> entity){
 *     entity.getJsonEntity();
 *     entity.getPersistedEntity();
 *       }
 *    }
 *         </pre>
 * 
 */
public class MappingAxon2HttpMessageConvertor extends AbstractHttpMessageConverter<Object> implements GenericHttpMessageConverter<Object>
{

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    Logger logger = LoggerFactory.getLogger(MappingAxon2HttpMessageConvertor.class);

    private AxonBuilder axonBuilder = new AxonBuilder();

    private ObjectMapper objectMapper = new ObjectMapper();

    PersistentManager persistentManager;

    public MappingAxon2HttpMessageConvertor()
    {
        super(new MediaType("application", "json", DEFAULT_CHARSET), new MediaType("application", "*+json", DEFAULT_CHARSET));
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType)
    {
        return canRead(clazz, null, mediaType);
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType)
    {
        if (mediaType == null || mediaType.isCompatibleWith(MediaType.APPLICATION_JSON))
            return true;

        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType)
    {
        if (mediaType == null || mediaType.isCompatibleWith(MediaType.APPLICATION_JSON))
            return true;

        return false;
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException
    {
        try
        {
            InputStream body = inputMessage.getBody();
            HttpHeaders headers = inputMessage.getHeaders();
            //
            byte[] bs = ByteStreams.toByteArray(body);
            InputStream beanInputStream = new ByteArrayInputStream(bs);
            IOUtils.closeQuietly(body);

            String jsonContent = IOUtils.toString(beanInputStream, "UTF-8");
            JavaType javaType = getJavaType(type, contextClass);
            Class<?> rawClass = null;
            if (javaType.hasGenericTypes())
                rawClass = (Class<?>) ((java.lang.reflect.ParameterizedType) type).getActualTypeArguments()[0];
            else
                rawClass = javaType.getRawClass();
            Object instance = rawClass.newInstance();
            if (StringUtil.isEmpty(jsonContent))
                return instance;
            Axon axon = new AxonBuilder().create();
            Object bean = axon.toObject(jsonContent, rawClass, instance);
            body.close();
            if (inputMessage.getClass().equals(org.springframework.http.server.ServletServerHttpRequest.class))
            {
                ServletServerHttpRequest sshr = (ServletServerHttpRequest) inputMessage;
                // HttpServletRequest servletRequest = sshr.getServletRequest();
                if (sshr.getMethod() == HttpMethod.PUT && persistentManager != null)
                {
                    if (bean instanceof AbstractEntity)
                    {
                        BasePO abstractEntity = (BasePO) bean;
                        if (abstractEntity.getId() == null || abstractEntity.getId().longValue() == 0)
                            throw new UnsupportedOperationException("entity is null");
                        Object entity = persistentManager.findById(rawClass, abstractEntity.getId());                        
                        if (entity == null)
                            throw new UnsupportedOperationException("entity not found");
                        return new EntityWrapper<Object>(bean, axon.toObject(jsonContent, bean.getClass(), entity));
                    }
                }

            }
            return bean;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    protected JavaType getJavaType(Type type, Class<?> contextClass)
    {
        return (contextClass != null) ? this.objectMapper.getTypeFactory().constructType(type, contextClass)
                : this.objectMapper.constructType(type);
    }

    @Override
    protected boolean supports(Class<?> clazz)
    {
        // should not be called, since we override canRead/Write instead
        throw new UnsupportedOperationException();
    }

    @Override
    // FIXME
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException
    {
        // should not be called, since we override canRead/Write instead
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeInternal(Object t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException
    {
        OutputStream body = outputMessage.getBody();
        HttpHeaders headers = outputMessage.getHeaders();
        if (headers != null)
        {
            // TODO : use header for filter
        }
        String json = axonBuilder.create().toJson(t);
        body.write(json.getBytes("UTF-8"));
        body.flush();
        IOUtils.closeQuietly(body);
    }

    public void setPersistentManager(PersistentManager persistentManager)
    {
        this.persistentManager = persistentManager;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void write(Object t, Type type, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException
    {
        // TODO Auto-generated method stub

    }

}
