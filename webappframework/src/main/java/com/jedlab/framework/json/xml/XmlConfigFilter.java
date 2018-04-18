package com.jedlab.framework.json.xml;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.omidbiz.core.axon.AxonBeanHelper;
import org.omidbiz.core.axon.Filter;
import org.omidbiz.core.axon.Property;
import org.omidbiz.core.axon.internal.PathProcessor;
import org.omidbiz.core.axon.internal.SerializationContext;

import com.jedlab.framework.reflections.ReflectionUtil;

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