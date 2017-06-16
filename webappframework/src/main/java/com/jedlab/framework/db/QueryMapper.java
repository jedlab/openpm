package com.jedlab.framework.db;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.apache.commons.beanutils.PropertyUtils;

import com.jedlab.framework.reflections.Property;
import com.jedlab.framework.reflections.ReflectionUtil;
import com.jedlab.framework.spring.web.Filter;
import com.jedlab.framework.util.CollectionUtil;

/**
 * @author omidp
 *
 */
public class QueryMapper
{

    public static <E> E toObject(List<Map<String, Object>> queryResult, Class<E> clz)
    {
        List<E> list = toList(queryResult, clz);
        if (CollectionUtil.isNotEmpty(list))
        {
            return list.iterator().next();
        }
        return null;
    }

    public static <E> List<E> toList(List<Map<String, Object>> queryResult, Class<E> clz)
    {
        List<E> instanceList = new ArrayList<E>();
        try
        {
            if (queryResult != null)
            {
                List<Property> properties = getProperties(clz);
                Map<String, String> maps = new HashMap<String, String>();
                if (clz.isAnnotationPresent(AttributeOverrides.class))
                {
                    findRealColumnWithAttributeOverrides(clz, maps);
                }
                if (clz.isAnnotationPresent(AttributeOverride.class))
                {
                    AttributeOverride annotation = clz.getAnnotation(AttributeOverride.class);
                    findRealColumnWithAttributeOverride(annotation, maps);
                }
                //
                for (Map<String, Object> dbRecord : queryResult)
                {

                    E instance = clz.newInstance();
                    for (Property prop : properties)
                    {
                        String columnName = prop.getColumnName();
                        if (maps.isEmpty() == false)
                        {
                            // do we need to check for every iteration ?
                            columnName = maps.get(columnName) == null ? columnName : maps.get(columnName);
                        }
                        Object recordValue = dbRecord.get(columnName);
                        Field field = ReflectionUtil.getField(clz, prop.getFieldName());
                        if (field != null && recordValue != null)
                        {
                            field.setAccessible(true);
                            if (ReflectionUtil.isPrimitive(field.getType()) || ReflectionUtil.isWrapper(field.getType()))
                            {
                                Object val = ReflectionUtil.toObject(field.getType(), recordValue);
                                ReflectionUtil.set(field, instance, val);
                            }
                            else if (field.getType().isEnum())
                            {
                                Enum enVal = Enum.valueOf((Class<Enum>) field.getType(), String.valueOf(recordValue));
                                ReflectionUtil.set(field, instance, enVal);
                            }
                            else
                            {
                                // field is relational entity
                                Object entityInstance = field.getType().newInstance();
                                if (entityInstance instanceof EntityModel)
                                {
                                    EntityModel po = (EntityModel) entityInstance;
                                    po.setId((Long) recordValue);
                                    ReflectionUtil.set(field, instance, entityInstance);
                                }
                            }
                        }
                    }
                    instanceList.add(instance);
                }
                return instanceList;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return instanceList;
    }

    private static Map<String, String> findRealColumnWithAttributeOverride(AttributeOverride attributeOverride, Map<String, String> maps)
    {

        String atrrName = attributeOverride.name();
        Column column = attributeOverride.column();
        maps.put(atrrName, column.name());
        return maps;
    }

    private static Map<String, String> findRealColumnWithAttributeOverrides(Class<?> clz, Map<String, String> maps)
    {
        if (clz.isAnnotationPresent(AttributeOverrides.class))
        {
            AttributeOverrides annotation = clz.getAnnotation(AttributeOverrides.class);
            AttributeOverride[] value = annotation.value();
            for (int i = 0; i < value.length; i++)
            {
                findRealColumnWithAttributeOverride(value[i], maps);
            }
        }
        return maps;
    }
    
    
    /**
     * used for getting Column name and filed name from entity
     * 
     * @param clz
     * @return
     */
    public static List<Property> getProperties(Class<?> clz)
    {
        List<Property> props = new ArrayList<Property>();
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clz);
        for (PropertyDescriptor propertyDescriptor : descriptors)
        {
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null && readMethod.isAnnotationPresent(Transient.class) == false)
            {
                Column annotation = readMethod.getAnnotation(Column.class);
                if (annotation != null)
                {
                    String dbColumnName = annotation.name();
                    props.add(new Property(dbColumnName, propertyDescriptor.getName()));
                }
                JoinColumn joinAnnotation = readMethod.getAnnotation(JoinColumn.class);
                if (joinAnnotation != null)
                {
                    String dbColumnName = joinAnnotation.name();
                    props.add(new Property(dbColumnName, propertyDescriptor.getName()));
                }
            }
        }
        return props;
    }

    public static Map<String, Object> filterMap(Filter filter)
    {
        Map<String, Object> filters = new WeakHashMap<String, Object>();
        Class<? extends Filter> clz = filter.getClass();
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clz);
        for (PropertyDescriptor pd : pds)
        {
            if(pd.getPropertyType().equals(Class.class))
                continue;
            try
            {
                Object invoke = pd.getReadMethod().invoke(filter, null);
                filters.put(pd.getName(), invoke);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        filters.values().removeIf(Objects::isNull);
        return filters;
    }

}
