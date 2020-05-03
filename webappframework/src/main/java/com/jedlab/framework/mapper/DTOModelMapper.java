package com.jedlab.framework.mapper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author omidp
 * 
 *         <pre>
 *         &#64;Override
 *         public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers)
 *         {
 *             ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().applicationContext(this.applicationContext).build();
 *             argumentResolvers.add(new DTOModelMapper(objectMapper, entityManager, modelMapper()));
 *         }
 *         </pre>
 * 
 *         <p>
 *         <b>How to use :</b> insteead of RequestBody Annotation write
 *         </p>
 * 
 *         <pre>
 *         DTO(value = YOURDTO.class, mapper = YOURENTITYMAPPER.class)
 *         </pre>
 */
public class DTOModelMapper extends RequestResponseBodyMethodProcessor
{

    private EntityManager entityManager;

    private ModelMapper modelMapper;

    private ApplicationContext context;

    public DTOModelMapper(ObjectMapper objectMapper, EntityManager entityManager, ModelMapper modelMapper)
    {
        super(Collections.singletonList(new MappingJackson2HttpMessageConverter(objectMapper)));
        this.entityManager = entityManager;
        this.modelMapper = modelMapper;
    }

    public DTOModelMapper(ObjectMapper objectMapper, EntityManager entityManager, ModelMapper modelMapper, ApplicationContext context)
    {
        super(Collections.singletonList(new MappingJackson2HttpMessageConverter(objectMapper)));
        this.entityManager = entityManager;
        this.modelMapper = modelMapper;
        this.context = context;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter)
    {
        return parameter.hasParameterAnnotation(DTO.class);
    }

    @Override
    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter)
    {
        binder.validate();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception
    {
        Object dto = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        Object id = getEntityId(dto);
        DTO dtoType = getDTOAnnotation(parameter);
        Class<? extends DTOPropertyMapper> propertyMap = null;
        if (dtoType != null)
        {
            propertyMap = dtoType.mapper();
        }
        if (id == null)
        {
            DTOPropertyMapper mapperBean = null;
            if (dtoType.springEnabled())
            {
                mapperBean = context.getBean(propertyMap);
            }
            else
            {
                mapperBean = propertyMap.newInstance();
            }
            return getMapping(mapperBean).map(dto);
        }
        else
        {
            Object persistedObject = entityManager.find(parameter.getParameterType(), id);
            DTOPropertyMapper mapperBean = null;
            if (dtoType.springEnabled())
            {
                mapperBean = context.getBean(propertyMap);
            }
            else
            {
                mapperBean = propertyMap.newInstance();
            }
            getMapping(mapperBean).map(dto, persistedObject);
            return persistedObject;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <S, D> PropertyMap<S, D> getMapping(DTOPropertyMapper dtoMapper) throws InstantiationException, IllegalAccessException
    {
        PropertyMap typeMap = modelMapper.getTypeMap(dtoMapper.getSourceType(), dtoMapper.getDestinationType());
        if (typeMap == null)
        {
            modelMapper.addMappings(dtoMapper);
            typeMap = modelMapper.getTypeMap(dtoMapper.getSourceType(), dtoMapper.getDestinationType());
        }
        return typeMap;
    }

    @Override
    protected Object readWithMessageConverters(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType)
            throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException
    {
        DTO dtoType = getDTOAnnotation(parameter);
        if (dtoType != null)
        {
            return super.readWithMessageConverters(inputMessage, parameter, dtoType.value());
        }
        throw new RuntimeException();
    }

    private DTO getDTOAnnotation(MethodParameter parameter)
    {
        for (Annotation ann : parameter.getParameterAnnotations())
        {
            DTO dtoType = AnnotationUtils.getAnnotation(ann, DTO.class);
            if (dtoType != null)
            {
                return dtoType;
            }
        }
        return null;
    }

    private Object getEntityId(@NotNull Object dto)
    {
        for (Field field : dto.getClass().getDeclaredFields())
        {
            if (field.getAnnotation(Id.class) != null)
            {
                try
                {
                    field.setAccessible(true);
                    return field.get(dto);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}