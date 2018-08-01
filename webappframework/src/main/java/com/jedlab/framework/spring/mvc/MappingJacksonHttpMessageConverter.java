package com.jedlab.framework.spring.mvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.io.ByteStreams;
import com.jedlab.framework.db.EntityModel;
import com.jedlab.framework.json.JacksonView;
import com.jedlab.framework.json.xml.CommonsConfig;
import com.jedlab.framework.reflections.ReflectionUtil;
import com.jedlab.framework.spring.rest.ResultList;
import com.jedlab.framework.util.StringUtil;

/**
 * @author omidp
 * <pre>
 * 
 * 
    @Bean
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public MappingJacksonHttpMessageConverter jacksnConverter()
    {
        MappingJacksonHttpMessageConverter axon = new MappingJacksonHttpMessageConverter(new ObjectMapper());
        return axon;
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        converters.add(jacksnConverter());   
    }
 * 
 * </pre>
 */
public class MappingJacksonHttpMessageConverter extends  AbstractGenericHttpMessageConverter<Object>
{

    private static final Logger logger = LoggerFactory.getLogger(MappingAxon2HttpMessageConverter.class);

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private ObjectMapper objectMapper;

    private PersistentManager persistentManager;

    public MappingJacksonHttpMessageConverter(ObjectMapper objectMapper)
    {
//        super(objectMapper);
        this.objectMapper = objectMapper;
    }
    
    

    public void setPersistentManager(PersistentManager persistentManager)
    {
        this.persistentManager = persistentManager;
    }



    public static class XmlPropertyFilter extends SimpleBeanPropertyFilter
    {

        private String startingViewName;
        private String startingBeanName;
        private CombinedConfiguration cc;
        private Queue<String> beans = new LinkedList<>();

        public XmlPropertyFilter(Class<?> rootClass, String viewName)
        {
            this.startingBeanName = rootClass.getSimpleName().toLowerCase();
            this.startingViewName = viewName;
            cc = CommonsConfig.getInstance().getCombinedConfig();
            cc.setExpressionEngine(new XPathExpressionEngine());
        }

        @Override
        public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception
        {
            String beanName = pojo.getClass().getSimpleName().toLowerCase();
            if (javassist.util.proxy.ProxyFactory.isProxyClass(pojo.getClass()))
            {
                logger.info(writer.getName() + " property is not fetched and is lazy  : " + startingBeanName
                        + "  : try to fix fixed the property " + pojo.getClass().getSuperclass());
                return;
            }
            if (startingBeanName.equals(beanName) == false)
            {
                startingBeanName = beanName;
                startingViewName = beans.poll();
            }
            if (ResultList.class.equals(pojo.getClass()))
            {
                super.serializeAsField(pojo, jgen, provider, writer);
                return;
            }
            String clzName = startingBeanName;
            String viewName = startingViewName == null ? "default" : startingViewName;
            String p = writer.getName();
            if (ReflectionUtil.isPrimitive(writer.getType().getRawClass()))
            {
                if (cc.getString(clzName + "/" + viewName + "/field[@name='" + p + "']/@name") != null)
                {
                    super.serializeAsField(pojo, jgen, provider, writer);
                    return;
                }
            }
            else
            {
                if (cc.getString(clzName + "/" + viewName + "/field[@name='" + p + "']/@name") != null)
                {
                    beans.add(cc.getString(beanName + "/" + viewName + "/field[@name='" + p + "']/@view", "simple"));
                    super.serializeAsField(pojo, jgen, provider, writer);
                    return;
                }
            }
        }

    }

    @Override
    protected void writeInternal(Object t, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException
    {
        OutputStream body = outputMessage.getBody();
        HttpHeaders headers = outputMessage.getHeaders();
        String json = "";
        if (headers != null)
        {
            String viewName = headers.get("X-VIEWNAME") != null ? headers.get("X-VIEWNAME").iterator().next() : "";
            if (StringUtil.isNotEmpty(viewName))
            {
                if (t instanceof ParameterizedType)
                {
                    ParameterizedType paramType = (ParameterizedType) t;
                    Class<?> clz = (Class<?>) paramType.getActualTypeArguments()[0];
                    json = objectMapper.writer(new SimpleFilterProvider().addFilter("JsonViewFilter", new XmlPropertyFilter(clz, viewName)))
                            .writeValueAsString(t);
                }
                else if (type instanceof ParameterizedType)
                {
                    ParameterizedType paramType = (ParameterizedType) type;
                    Class<?> clz = (Class<?>) paramType.getActualTypeArguments()[0];
                    json = objectMapper.writer(new SimpleFilterProvider().addFilter("JsonViewFilter", new XmlPropertyFilter(clz, viewName)))
                            .writeValueAsString(t);
                }
            }
        }

        if (StringUtil.isEmpty(json))
        {
            Class<?> declaringClass = AnnotationUtils.findAnnotationDeclaringClass(JsonFilter.class, t.getClass());
            if (declaringClass != null)
                json = objectMapper
                        .writer(new SimpleFilterProvider().addFilter("JsonViewFilter", SimpleBeanPropertyFilter.serializeAllExcept("")))
                        .writeValueAsString(t);
            else
                json = objectMapper.writeValueAsString(t);
        }
        body.write(json.getBytes("UTF-8"));
        body.flush();
        IOUtils.closeQuietly(body);
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
        {
            return clazz.isAnnotationPresent(JacksonView.class);
        }

        return false;
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException
    {
        JavaType javaType = getJavaType(type, contextClass);
        return readJavaType(javaType, inputMessage, contextClass);
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException
    {
        JavaType javaType = getJavaType(clazz, null);
        return readJavaType(javaType, inputMessage, clazz);
    }

    protected JavaType getJavaType(Type type, Class<?> contextClass)
    {
        return (contextClass != null) ? this.objectMapper.getTypeFactory().constructType(type, contextClass)
                : this.objectMapper.constructType(type);
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage, Class<?> contextClass)
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
                Type type = contextClass.getGenericSuperclass();
                if (type instanceof ParameterizedType)
                {
                    rawClass = (Class<?>) ((java.lang.reflect.ParameterizedType) type).getActualTypeArguments()[0];
                }
                else
                {
                    throw new IllegalArgumentException("Could not guess entity class by reflection");
                }
            }
            else
            {
                rawClass = javaType.getRawClass();
            }
            Object instance = rawClass.newInstance();
            if (StringUtil.isEmpty(jsonContent))
                return instance;
            // Object bean = axon.toObject(jsonContent, rawClass, instance);
            Object bean = objectMapper.readValue(jsonContent, rawClass);
            body.close();
            if (bean instanceof EntityModel)
            {
                EntityModel<Long> abstractEntity = (EntityModel<Long>) bean;
                if (abstractEntity.getId() != null && persistentManager != null)
                {
                    Object entity = persistentManager.findById(rawClass, abstractEntity.getId());
                    if (entity == null)
                        throw new UnsupportedOperationException("entity not found");
                    return new EntityWrapper<Object>(bean, objectMapper.readerForUpdating(entity).readValue(jsonContent));
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

}
