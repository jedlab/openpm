package com.jedlab.framework.reflections;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;

import com.jedlab.framework.util.StringUtil;
import com.jedlab.framework.web.AbstractActionBean;

/**
 * @author Omid Pourhadi : omidpourhadi [AT] gmail [DOT] com
 * 
 */
public class ReflectionUtil
{
    
    private static final Logger logger = Logger.getLogger(ReflectionUtil.class.getName());

    private static final Class<?>[] WRAPPER_TYPES = { int.class, long.class, short.class, float.class, double.class, byte.class,
            boolean.class, char.class };

    public static List<Field> getFields(Class clazz)
    {
        List<Field> fields = new ArrayList<Field>();
        for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass())
        {
            for (Field field : superClass.getDeclaredFields())
            {
                fields.add(field);
            }
        }
        return fields;
    }

    public static <E> ArrayList<E> newArrayList(E... elements)
    {
        ArrayList<E> list = new ArrayList<E>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }

    public static Class<?> getGenericFieldClassType(Field field) throws NoSuchFieldException, SecurityException
    {
        field.setAccessible(true);
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Class<?> genericClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        return genericClass;
    }

    public static Field getField(Class clazz, String name)
    {
        for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass())
        {
            try
            {
                return superClass.getDeclaredField(name);
            }
            catch (NoSuchFieldException nsfe)
            {
                logger.info(name + " field not found");
            }
        }
        return null;
    }

    public static Collection<?> instantiateCollection(Class<?> t)
    {

        if (t == Set.class)
        {
            return new HashSet<Object>();
        }
        else if (t == List.class)
        {
            return new ArrayList<Object>();
        }
        else if (t == Map.class)
        {
            throw new RuntimeException("can not instantiate map");
        }
        else if (t == Vector.class)
        {
            throw new RuntimeException("can not instantiate vector");
        }
        else
            throw new RuntimeException("unknown type");
    }

    public static void set(Field field, Object target, Object value) throws Exception
    {
        try
        {
            field.set(target, value);
        }
        catch (IllegalArgumentException iae)
        {
            // target may be null if field is static so use
            // field.getDeclaringClass() instead
            String message = "Could not set field value by reflection: " + field + " on: " + field.getDeclaringClass().getName();
            if (value == null)
            {
                message += " with null value";
            }
            else
            {
                message += " with value: " + value.getClass();
            }
            throw new IllegalArgumentException(message, iae);
        }
    }

    public static boolean isPrimitive(Class type)
    {
        return primitiveTypeFor(type) != null;
    }

    public static boolean isWrapper(Class<?> clazz)
    {
        if (clazz == null)
            throw new RuntimeException("null value");
        for (int i = 0; i < WRAPPER_TYPES.length; i++)
        {
            if (clazz == WRAPPER_TYPES[i])
                return true;
        }
        return false;
    }

    public static boolean isArrayOrCollection(Class<?> clazz)
    {
        if (clazz == null)
            throw new RuntimeException("null value");
        return clazz.isArray() || isSubclass(clazz, Collection.class);
    }

    public static boolean isMap(Class<?> clazz)
    {
        if (clazz == null)
            throw new RuntimeException("null value");
        return isSubclass(clazz, Map.class);
    }

    public static boolean isEnum(Class<?> clazz)
    {
        if (clazz == null)
            throw new RuntimeException("null value");
        return clazz.isEnum();
    }

    public static boolean isSubclass(Class<?> class1, Class<?> class2)
    {
        List<Class<?>> superClasses = getAllSuperclasses(class1);
        List<Class<?>> superInterfaces = getAllInterfaces(class1);
        for (Class<?> c : superClasses)
        {
            if (class2 == c)
                return true;
        }
        for (Class<?> c : superInterfaces)
        {
            if (class2 == c)
                return true;
        }
        return false;
    }

    public static List getAllSuperclasses(Class cls)
    {
        if (cls == null)
        {
            return null;
        }
        List classes = new ArrayList();
        Class superclass = cls.getSuperclass();
        while (superclass != null)
        {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    public static List getAllInterfaces(Class cls)
    {
        if (cls == null)
        {
            return null;
        }
        List list = new ArrayList();
        while (cls != null)
        {
            Class[] interfaces = cls.getInterfaces();
            for (int i = 0; i < interfaces.length; i++)
            {
                if (list.contains(interfaces[i]) == false)
                {
                    list.add(interfaces[i]);
                }
                List superInterfaces = getAllInterfaces(interfaces[i]);
                for (Iterator it = superInterfaces.iterator(); it.hasNext();)
                {
                    Class intface = (Class) it.next();
                    if (list.contains(intface) == false)
                    {
                        list.add(intface);
                    }
                }
            }
            cls = cls.getSuperclass();
        }
        return list;
    }

    public static Class primitiveTypeFor(Class wrapper)
    {
        if (wrapper == Boolean.class)
            return Boolean.TYPE;
        if (wrapper == Byte.class)
            return Byte.TYPE;
        if (wrapper == Character.class)
            return Character.TYPE;
        if (wrapper == Short.class)
            return Short.TYPE;
        if (wrapper == BigDecimal.class)
            return BigDecimal.class;
        if (wrapper == Date.class)
            return Date.class;
        if (wrapper == java.sql.Date.class)
            return java.sql.Date.class;
        if (wrapper == Integer.class)
            return Integer.TYPE;
        if (wrapper == Long.class)
            return Long.TYPE;
        if (wrapper == Float.class)
            return Float.TYPE;
        if (wrapper == Double.class)
            return Double.TYPE;
        if (wrapper == Void.class)
            return Void.TYPE;
        if (wrapper == String.class)
            return String.class;
        return null;
    }

    public static Object toObject(Class clazz, Object value)
    {
        if (value == null)
            return null;
        if (value instanceof String)
        {
            String v = (String) value;
            if (StringUtil.isEmpty(v))
                return null;
        }
        if (Boolean.class == clazz || boolean.class == clazz)
            return (Boolean) value;
        if (Date.class == clazz)
        {
            if (value instanceof String)
            {
                throw new IllegalArgumentException("persian date can not be handled yet");
            }
            else
            {
                return (Date) value;
            }
        }
        if (Byte.class == clazz || byte.class == clazz)
            return Byte.parseByte(String.valueOf(value));
        if (Short.class == clazz || short.class == clazz)
            return Short.parseShort(String.valueOf(value));
        if (BigDecimal.class == clazz)
            return new BigDecimal(String.valueOf(value));
        if (Integer.class == clazz || int.class == clazz)
            return Integer.parseInt(String.valueOf(value));
        if (Long.class == clazz || long.class == clazz)
            return Long.parseLong(String.valueOf(value));
        if (Float.class == clazz || float.class == clazz)
            return Float.parseFloat(String.valueOf(value));
        if (Double.class == clazz || double.class == clazz)
            return Double.parseDouble(String.valueOf(value));
        if (String.class == clazz)
            return String.valueOf(value);
        return value;
    }

    
    public static <T> T cast(Object value, Class<T> clz)
    {
        if (value == null)
            return null;
        try
        {
            return clz.cast(value);

        }
        catch (ClassCastException e)
        {
            return null;
        }
    }

    public static <T> T cloneBean(Object source, Class<T> target, String... ignoreProperties)
    {
        T clone = null;
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;
        try
        {
            clone = target.newInstance();
            if (source == null)
                return clone;
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(target);
            if (propertyDescriptors != null)
            {
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors)
                {
                    if (propertyDescriptor.getWriteMethod() != null
                            && (ignoreProperties == null || (!ignoreList.contains(propertyDescriptor.getName()))))
                    {
                        if (PropertyUtils.isReadable(source, propertyDescriptor.getName()))
                        {
                            Object propertyValue = PropertyUtils.getProperty(source, propertyDescriptor.getName());
                            if (PropertyUtils.isWriteable(clone, propertyDescriptor.getName()))
                                PropertyUtils.setProperty(clone, propertyDescriptor.getName(), propertyValue);
                        }
                    }
                }
            }
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return clone;
    }

    public static <T> T cloneBean(Class<T> target, Object source, String... withProperties)
    {
        T clone = null;
        List<String> ignoreList = (withProperties != null && withProperties.length > 0) ? Arrays.asList(withProperties) : null;
        try
        {
            clone = target.newInstance();
            if (source == null)
                return clone;
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(target);
            if (propertyDescriptors != null)
            {
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors)
                {
                    if (propertyDescriptor.getWriteMethod() != null
                            && (ignoreList == null || (ignoreList.contains(propertyDescriptor.getName()))))
                    {
                        if (PropertyUtils.isReadable(source, propertyDescriptor.getName()))
                        {
                            Object propertyValue = PropertyUtils.getProperty(source, propertyDescriptor.getName());
                            if (PropertyUtils.isWriteable(clone, propertyDescriptor.getName()))
                                PropertyUtils.setProperty(clone, propertyDescriptor.getName(), propertyValue);
                        }
                    }
                }
            }
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return clone;
    }

    public static Object cloneBeanInstance(Object target, Object source, String... ignoreProperties)
    {
        if (target == null || source == null)
            throw new NullPointerException();
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;
        try
        {
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(target);
            if (propertyDescriptors != null)
            {
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors)
                {
                    if (propertyDescriptor.getWriteMethod() != null
                            && (ignoreProperties == null || (!ignoreList.contains(propertyDescriptor.getName()))))
                    {
                        if (PropertyUtils.isReadable(source, propertyDescriptor.getName()))
                        {
                            Object propertyValue = PropertyUtils.getProperty(source, propertyDescriptor.getName());
                            if (PropertyUtils.isWriteable(target, propertyDescriptor.getName()))
                                PropertyUtils.setProperty(target, propertyDescriptor.getName(), propertyValue);
                        }
                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return target;
    }

    public static Object cloneBeanInstance(Object target, Object source, boolean excludeNull, String... ignoreProperties)
    {
        if (target == null || source == null)
            throw new NullPointerException();
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;
        try
        {
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(target);
            if (propertyDescriptors != null)
            {
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors)
                {
                    if (propertyDescriptor.getWriteMethod() != null
                            && (ignoreProperties == null || (!ignoreList.contains(propertyDescriptor.getName()))))
                    {
                        if (PropertyUtils.isReadable(source, propertyDescriptor.getName()))
                        {
                            Object propertyValue = PropertyUtils.getProperty(source, propertyDescriptor.getName());
                            if (excludeNull == false)
                            {
                                if (PropertyUtils.isWriteable(target, propertyDescriptor.getName()))
                                    PropertyUtils.setProperty(target, propertyDescriptor.getName(), propertyValue);
                            }
                            else
                            {
                                if (propertyValue != null)
                                {
                                    if (PropertyUtils.isWriteable(target, propertyDescriptor.getName()))
                                        PropertyUtils.setProperty(target, propertyDescriptor.getName(), propertyValue);
                                }
                            }
                        }
                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return target;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Object> E handleNull(E obj, Class<E> clz)
    {
        if (obj != null)
            return obj;

        if (clz == Long.class || clz == long.class)
            return (E) new Long(0);
        if (clz == Double.class || clz == double.class)
            return (E) new Double(0);
        if (clz == BigDecimal.class)
            return (E) BigDecimal.ZERO;
        if (clz == Integer.class || clz == int.class)
            return (E) new Integer(0);
        if (clz == Float.class || clz == float.class)
            return (E) new Float(0);
        if (clz == Short.class || clz == short.class)
            return (E) new Short((short) 0);
        if (clz == Byte.class || clz == byte.class)
            return (E) new Byte((byte) 0);
        if (clz == Boolean.class || clz == boolean.class)
            return (E) new Boolean(false);
        if (clz == Character.class || clz == char.class)
            return (E) new Character(' ');
        if (clz == String.class)
            return (E) new String("");
        throw new IllegalArgumentException("class not supported : " + clz.getName());
    }

    public static Class wrapperToPrimitive(Class<?> clz)
    {
        if (clz == Long.class || clz == long.class)
            return Long.class;
        if (clz == Double.class || clz == double.class)
            return Double.class;
        if (clz == BigDecimal.class)
            return BigDecimal.class;
        if (clz == Integer.class || clz == int.class)
            return Integer.class;
        if (clz == Float.class || clz == float.class)
            return Float.class;
        if (clz == Short.class || clz == short.class)
            return Short.class;
        if (clz == Byte.class || clz == byte.class)
            return Byte.class;
        if (clz == Boolean.class || clz == boolean.class)
            return Boolean.class;
        if (clz == Character.class || clz == char.class)
            return Character.class;
        if (clz == String.class)
            return String.class;
        return null;
    }
    
    public static boolean isCollection(Object value)
    {
        if (value instanceof Collection)
        {
            return true;
        }
        return false;
    }
    
    public static Class<?> getGenericMethodClassType(Class<?> clz, Method method)
    {        
        Type type = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
        if(type instanceof TypeVariable)
            return null;
        Class<?> clazz = (Class<?>) type;
        return clazz;
    }

}
