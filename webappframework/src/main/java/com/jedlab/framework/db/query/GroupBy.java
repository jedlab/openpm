package com.jedlab.framework.db.query;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.jedlab.framework.db.query.GroupBy.GroupByProperty;

/**
 * @author omid
 *
 */
public class GroupBy implements Iterable<GroupByProperty>, Serializable
{

    private List<GroupByProperty> properties;

    public GroupBy(GroupByProperty... properties)
    {
        this(Arrays.asList(properties));
    }

    public GroupBy(List<GroupByProperty> props)
    {
        if (null == props || props.isEmpty())
        {
            throw new IllegalArgumentException("You have to provide at least one group by property !");
        }

        this.properties = props;
    }

    @Override
    public Iterator<GroupByProperty> iterator()
    {
        return this.properties.iterator();
    }

    public static class GroupByProperty implements Serializable
    {
        private String propertyName;

        public GroupByProperty(String propertyName)
        {
            this.propertyName = propertyName;
        }

        public String getPropertyName()
        {
            return propertyName;
        }

    }
}
