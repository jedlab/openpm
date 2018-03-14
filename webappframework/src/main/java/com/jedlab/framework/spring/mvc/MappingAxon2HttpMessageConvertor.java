package com.jedlab.framework.spring.mvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.omidbiz.core.axon.Axon;
import org.omidbiz.core.axon.AxonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.jedlab.framework.db.EntityModel;
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
public class MappingAxon2HttpMessageConvertor extends AbstractGenericHttpMessageConverter<Object>
{

    private static final Logger logger = LoggerFactory.getLogger(MappingAxon2HttpMessageConvertor.class);

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Axon axon = new AxonBuilder().create();

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
        JavaType javaType = getJavaType(type, contextClass);
        return readJavaType(javaType, inputMessage);
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException
    {
        JavaType javaType = getJavaType(clazz, null);
        return readJavaType(javaType, inputMessage);
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage)
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
            Class<?> rawClass = null;
            if (javaType.hasGenericTypes())
            {
                Type t = javaType.getRawClass();
                rawClass = (Class<?>) ((java.lang.reflect.ParameterizedType) t).getActualTypeArguments()[0];
            }
            else
            {
                rawClass = javaType.getRawClass();
            }
            Object instance = rawClass.newInstance();
            if (StringUtil.isEmpty(jsonContent))
                return instance;
            Object bean = axon.toObject(jsonContent, rawClass, instance);
            body.close();
            if (inputMessage.getClass().equals(org.springframework.http.server.ServletServerHttpRequest.class))
            {
                ServletServerHttpRequest sshr = (ServletServerHttpRequest) inputMessage;
                // HttpServletRequest servletRequest = sshr.getServletRequest();
                if (sshr.getMethod() == HttpMethod.PUT && persistentManager != null)
                {
                    if (bean instanceof EntityModel)
                    {
                        EntityModel<Long> abstractEntity = (EntityModel<Long>) bean;
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
    protected void writeInternal(Object t, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException
    {
        OutputStream body = outputMessage.getBody();
        HttpHeaders headers = outputMessage.getHeaders();
        if (headers != null)
        {
            // TODO : use header for filter
        }
        String json = axon.toJson(t);
        body.write(json.getBytes("UTF-8"));
        body.flush();
        IOUtils.closeQuietly(body);

    }

    public void setPersistentManager(PersistentManager persistentManager)
    {
        this.persistentManager = persistentManager;
    }

}
