package com.jedlab.framework.db;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Sort;

import com.jedlab.framework.reflections.Property;
import com.jedlab.framework.reflections.ReflectionUtil;
import com.jedlab.framework.spring.rest.QueryWhereParser;
import com.jedlab.framework.spring.rest.QueryWhereParser.FilterProperty;
import com.jedlab.framework.spring.web.Filter;
import com.jedlab.framework.spring.web.ParamOperator;
import com.jedlab.framework.spring.web.ParameterItem;
import com.jedlab.framework.spring.web.QParam;
import com.jedlab.framework.util.CollectionUtil;
import com.jedlab.framework.util.StringUtil;

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
//                                    po.setId((Long) recordValue);
                                    Method[] m = po.getClass().getMethods();
                                    if(m != null)
                                    {
                                        for (Method method : m)
                                        {
                                            if(method.getName().equals("setId"))
                                            {
                                                Class<?> c = method.getParameterTypes()[0];
                                                method.invoke(po, ReflectionUtil.toObject(c, recordValue));
                                            }
                                        }
                                    }
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
        if(props.isEmpty())
        {
            //check for field
            List<Field> fields = ReflectionUtil.getFields(clz);
            for (Field field : fields)
            {
                Column annotation = field.getAnnotation(Column.class);
                if(annotation != null)
                {
                    String dbColumnName = annotation.name();
                    props.add(new Property(dbColumnName, field.getName()));
                }
                JoinColumn joinAnnotation = field.getAnnotation(JoinColumn.class);
                if (joinAnnotation != null)
                {
                    String dbColumnName = joinAnnotation.name();
                    props.add(new Property(dbColumnName, field.getName()));
                }
            }
        }
        return props;
    }

    @Deprecated
    public static void filterMap(Filter filter, Criteria criteria)
    {
        List<ParameterItem> filterItems = new ArrayList<>();
        Class<? extends Filter> clz = filter.getClass();
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clz);
        for (PropertyDescriptor pd : pds)
        {
            if (pd.getPropertyType().equals(Class.class))
                continue;
            try
            {
                Method getter = pd.getReadMethod();
                if (getter != null && getter.isAnnotationPresent(QParam.class))
                {
                    QParam annotation = getter.getAnnotation(QParam.class);
                    Object invoke = pd.getReadMethod().invoke(filter, null);
                    String propertyName = pd.getName();
                    if(StringUtil.isNotEmpty(annotation.propertyName()))
                        propertyName = annotation.propertyName();
                    filterItems.add(new ParameterItem(propertyName, annotation.operator(), invoke));
                }
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        for (ParameterItem item : filterItems)
        {
            Object val = item.getValue();
            if(val == null)
                continue;
            if(val instanceof String && StringUtil.isEmpty(String.valueOf(val)))
            {                   
                continue;
            }
            if(val instanceof Date)
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime((Date)val);
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                val = cal.getTime();
            }
            if (ParamOperator.LIKE.equals(item.getOperator()))
            {
                criteria.add(Restrictions.ilike(item.getPropertyName(), "%" + item.getValue() + "%"));
            }
            if (ParamOperator.EQ.equals(item.getOperator()))
            {
                criteria.add(Restrictions.eq(item.getPropertyName(), item.getValue()));
            }
            if (ParamOperator.NEQ.equals(item.getOperator()))
            {
                criteria.add(Restrictions.ne(item.getPropertyName(), item.getValue()));
            }
            if (ParamOperator.GT.equals(item.getOperator()))
            {
                criteria.add(Restrictions.gt(item.getPropertyName(), item.getValue()));
            }
            if (ParamOperator.LT.equals(item.getOperator()))
            {
                criteria.add(Restrictions.lt(item.getPropertyName(), item.getValue()));
            }
            if (ParamOperator.LTE.equals(item.getOperator()))
            {
                criteria.add(Restrictions.le(item.getPropertyName(), item.getValue()));
            }
            if (ParamOperator.GTE.equals(item.getOperator()))
            {
                criteria.add(Restrictions.ge(item.getPropertyName(), item.getValue()));
            }
            if (ParamOperator.SQLQUERY.equals(item.getOperator()))
            {
                criteria.add(Restrictions.sqlRestriction(String.valueOf(item.getValue())));
            }
        }        
    }
    
    
    /**
     * build query parameters from sort instance to keep it in pagination
     * @param sort
     * @return
     */
    public static String sortQueryParams(Sort sort)
    {
        if(sort == null)
            return null;
        StringBuilder sb = new StringBuilder();
        sort.forEach(s->{
            sb.append("&").append("sort=");
            sb.append(s.getProperty()).append(",").append(s.isAscending() ? "asc" : "desc");
        });
        return sb.toString();
    }
    
    /**
     * build query parameters from filter instance to keep it in pagination
     * @param filter
     * @return
     */
    public static String queryParams(Filter filter)
    {
        Class<? extends Filter> clz = filter.getClass();
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clz);
        StringBuilder sb = new StringBuilder();
        for (PropertyDescriptor pd : pds)
        {
            if (pd.getPropertyType().equals(Class.class))
                continue;
            try
            {
                Method getter = pd.getReadMethod();
                if (getter != null)
                {
                    Object val = pd.getReadMethod().invoke(filter, null);
                    String propertyName = pd.getName();
                    if (val == null)
                        continue;
                    if (val instanceof String && StringUtil.isEmpty(String.valueOf(val)))
                    {
                        continue;
                    }
                    sb.append("&");
                    sb.append(propertyName).append("=").append(val);
                }
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        if (StringUtil.isEmpty(sb.toString()))
            return null;
        return sb.toString();
    }

    public static List<Predicate> filterMap(List<FilterProperty> filterProperties, CriteriaBuilder cb, CriteriaQuery criteria, Root root, Class<?> clz)
    {
        List<Predicate> predicateList = new ArrayList<Predicate>();
        for (int i = 0; i < filterProperties.size(); i++)
        {
            FilterProperty item = filterProperties.get(i);
            String property = item.getPropertyName();
            Object value = item.getValue();
            String operator = item.getOperator();            
            if(value == null)
                continue;
            if(value instanceof String && StringUtil.isEmpty(String.valueOf(value)))
            {                   
                continue;
            }
            if (property.indexOf(".") > 0)
            {
                
            }
            else
            {
//                ParameterExpression<?> pe = cb.parameter(String.class, property);
                Field field = ReflectionUtil.getField(clz, property);
                if (QueryWhereParser.EQ.equals(operator))
                {
                    if(field.getType().isEnum())
                    {
                        Enum valueOf = Enum.valueOf((Class<Enum>) field.getType(), String.valueOf(value));
                        predicateList.add(cb.equal(root.get(property), valueOf));
                    }
                    else
                    {
                        predicateList.add(cb.equal(root.get(property), value));
                    }
                }                    
                else if (QueryWhereParser.NEQ.equals(operator))
                {
                    if(field.getType().isEnum())
                    {
                        Enum valueOf = Enum.valueOf((Class<Enum>) field.getType(), String.valueOf(value));
                        predicateList.add(cb.not(cb.equal(root.get(property), valueOf)));
                    }
                    else
                    {
                        predicateList.add(cb.not(cb.equal(root.get(property), value)));
                    }
                }
                else if (QueryWhereParser.LK.equals(operator))
                    predicateList.add(cb.like(root.<String> get(property), "%" + value.toString() + "%"));
                else if (QueryWhereParser.BW.equals(operator))
                    predicateList.add(cb.like(root.<String> get(property), value.toString() + "%"));
                else if (QueryWhereParser.EW.equals(operator))
                    predicateList.add(cb.like(root.<String> get(property), "%" + value.toString()));
                else if (QueryWhereParser.GT.equals(operator))
                {
                    if(Date.class.isAssignableFrom(field.getType()))
                    {
                        predicateList.add(cb.greaterThan(root.<Date> get(property), (Date)value));
                    }
                    else if(Number.class.isAssignableFrom(field.getType()))
                    {
                        String v = String.valueOf(value);
                        predicateList.add(cb.greaterThan(root.<Long> get(property), Long.parseLong(v)));
                    }                    
                }
                else if (QueryWhereParser.GTE.equals(operator))
                {
                    if(Date.class.isAssignableFrom(field.getType()))
                    {

                        predicateList.add(cb.greaterThanOrEqualTo(root.<Date> get(property), (Date)value ));
                    }
                    else if(Number.class.isAssignableFrom(field.getType()))
                    {
                        String v = String.valueOf(value);
                        predicateList.add(cb.greaterThanOrEqualTo(root.<Long> get(property), Long.parseLong(v)));
                    }
                }
                else if (QueryWhereParser.LT.equals(operator))
                {
                    if(Date.class.isAssignableFrom(field.getType()))
                    {

                        predicateList.add(cb.lessThan(root.<Date> get(property), (Date) value));
                    }
                    else if(Number.class.isAssignableFrom(field.getType()))
                    {
                        String v = String.valueOf(value);
                        predicateList.add(cb.lessThan(root.<Long> get(property), Long.parseLong(v)));
                    }
                }
                else if (QueryWhereParser.LTE.equals(operator))
                {
                    if(Date.class.isAssignableFrom(field.getType()))
                    {

                        predicateList.add(cb.lessThanOrEqualTo(root.<Date> get(property), (Date) value));
                    }
                    else if(Number.class.isAssignableFrom(field.getType()))
                    {
                        String v = String.valueOf(value);
                        predicateList.add(cb.lessThanOrEqualTo(root.<Long> get(property), Long.parseLong(v)));
                    }
                }
                else if(QueryWhereParser.IN.equals(operator) && value instanceof List)
                {
                    predicateList.add(cb.in(root.get(property)).value(value));
                }
            }
            
        }
       return predicateList;
    }
    
    
    public static List<Predicate> filterMap(Filter filter, CriteriaBuilder cb, CriteriaQuery criteria, Root root)
    {
        List<ParameterItem> filterItems = new ArrayList<>();
        Class<? extends Filter> clz = filter.getClass();
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clz);
        for (PropertyDescriptor pd : pds)
        {
            if (pd.getPropertyType().equals(Class.class))
                continue;
            try
            {
                Method getter = pd.getReadMethod();
                if (getter != null && getter.isAnnotationPresent(QParam.class))
                {
                    QParam annotation = getter.getAnnotation(QParam.class);
                    Object invoke = pd.getReadMethod().invoke(filter, null);
                    String propertyName = pd.getName();
                    if(StringUtil.isNotEmpty(annotation.propertyName()))
                        propertyName = annotation.propertyName();
                    filterItems.add(new ParameterItem(propertyName, annotation.operator(), invoke));
                }
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        //
        List<Predicate> predicateList = new ArrayList<Predicate>();
        for (ParameterItem item : filterItems)
        {
            Object val = item.getValue();
            if(val == null)
                continue;
            if(val instanceof String && StringUtil.isEmpty(String.valueOf(val)))
            {                   
                continue;
            }
            if(val instanceof Date)
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime((Date)val);
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                val = cal.getTime();
            }
            if (ParamOperator.LIKE.equals(item.getOperator()))
            {
                predicateList.add(cb.like(root.<String> get(item.getPropertyName()), "%" + val.toString() + "%"));
            }
            if (ParamOperator.EQ.equals(item.getOperator()))
            {
                predicateList.add(cb.equal(root.get(item.getPropertyName()), val));
            }
            if (ParamOperator.NEQ.equals(item.getOperator()))
            {
                predicateList.add(cb.not(cb.equal(root.get(item.getPropertyName()), val)));
            }
            if (ParamOperator.GT.equals(item.getOperator()))
            {
                predicateList.add(cb.greaterThan(root.<Long> get(item.getPropertyName()), (Long)val));
            }
            if (ParamOperator.LT.equals(item.getOperator()))
            {
                predicateList.add(cb.lessThan(root.<Long> get(item.getPropertyName()), (Long)val));
            }
            if (ParamOperator.LTE.equals(item.getOperator()))
            {
                predicateList.add(cb.lessThanOrEqualTo(root.<Long>get(item.getPropertyName()), (Long)val));
            }
            if (ParamOperator.GTE.equals(item.getOperator()))
            {
                predicateList.add(cb.greaterThanOrEqualTo(root.<Long>get(item.getPropertyName()), (Long)val));
            }
            if (ParamOperator.SQLQUERY.equals(item.getOperator()))
            {
                throw new UnsupportedOperationException("not supported in JPA");
//                criteria.add(Restrictions.sqlRestriction(String.valueOf(item.getValue())));
            }
        }        
        return predicateList;
    }
    
    

}
