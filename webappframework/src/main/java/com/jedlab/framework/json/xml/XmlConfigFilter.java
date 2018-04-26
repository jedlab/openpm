package com.jedlab.framework.json.xml;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.omidbiz.core.axon.AxonBeanHelper;
import org.omidbiz.core.axon.Filter;
import org.omidbiz.core.axon.Property;
import org.omidbiz.core.axon.internal.PathProcessor;
import org.omidbiz.core.axon.internal.SerializationContext;

import com.jedlab.framework.db.EntityModel;
import com.jedlab.framework.reflections.ReflectionUtil;
import com.jedlab.framework.spring.rest.ResultList;

public class XmlConfigFilter implements Filter
{

    private final String startingViewName;
    private final String startingBeanName;
    private Class<?> rootClass;
    CombinedConfiguration cc;

    public XmlConfigFilter(Class<?> rootClass, String viewName)
    {
        this.rootClass = rootClass;
        this.startingBeanName = this.rootClass.getSimpleName().toLowerCase();
        this.startingViewName = viewName;
        cc = CommonsConfig.getInstance().getCombinedConfig();
        cc.setExpressionEngine(new XPathExpressionEngine());
    }

    @Override
    public void beforeFilter(SerializationContext ctx)
    {
    }

    @Override
    public boolean exclude(String path, Object target, Property property, Object propertyValue)
    {
        if (property != null && property.getGetter() != null)
        {

            String viewName = startingViewName;
            String beanName = startingBeanName;
            Class<?> clz = rootClass;
            String[] paths = path.split("\\.");
            String p = paths[paths.length - 1];

            boolean iscollection = p.contains("[]");
            p = p.replace("resultList[]", "").replace("[]", "").replace("result", "");

            if (!p.isEmpty())
            {
                
                if (cc.getString(beanName + "/" + viewName + "/field[@name='" + p + "']/@name") == null)
                {
                    return true;
                }
                viewName = cc.getString(beanName + "/" + viewName + "/field[@name='" + p + "']/@view", "simple");
                Property prop = AxonBeanHelper.getProperty(clz, true, p);
                if(prop == null)
                    prop = property;
                if (iscollection)
                {
                    clz = ReflectionUtil.getGenericMethodClassType(clz, prop.getGetter());
                }
                else
                {
                    clz = prop.getType();
                }
            }
            if(clz == null)
                clz = rootClass;
            beanName = clz.getSimpleName().toLowerCase();

            if (cc.getString(beanName + "/" + viewName + "/field[@name='" + property.getName() + "']/@name") == null)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterFilter()
    {

    }

}