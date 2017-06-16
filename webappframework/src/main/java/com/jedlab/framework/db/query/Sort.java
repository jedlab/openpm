package com.jedlab.framework.db.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.jedlab.framework.db.query.Sort.Order;

/**
 * @author omid
 * 
 */
public class Sort implements Iterable<Order>, Serializable
{

    public static final Direction DEFAULT_DIRECTION = Direction.ASC;

    public Sort(Order... orders)
    {
        this(Arrays.asList(orders));
    }

    public Sort(List<Order> orders)
    {

        if (null == orders || orders.isEmpty())
        {
            throw new IllegalArgumentException("You have to provide at least one sort property to sort by!");
        }

        this.orders = orders;
    }

    public Sort(String... properties)
    {
        this(DEFAULT_DIRECTION, properties);
    }

    public Sort(Direction direction, String... properties)
    {
        this(direction, properties == null ? new ArrayList<String>() : Arrays.asList(properties));
    }

    public Sort(Direction direction, List<String> properties)
    {

        if (properties == null || properties.isEmpty())
        {
            throw new IllegalArgumentException("You have to provide at least one property to sort by!");
        }

        this.orders = new ArrayList<Order>(properties.size());

        for (String property : properties)
        {
            this.orders.add(new Order(direction, property));
        }
    }

    public static enum Direction {
        ASC("asc"), DESC("desc"), NULLS_FIRST("NULLS FIRST"), NULLS_LAST("NULLS LAST"), QUERY("query")
        , DESC_NULLS_LAST("DESC NULLS LAST"), DESC_NULLS_FIRST("DESC NULLS FIRST");

        private String label;

        private Direction(String label)
        {
            this.label = label;
        }

        public String getLabel()
        {
            return label;
        }

    }

    private List<Order> orders;

    @Override
    public Iterator<Order> iterator()
    {
        return this.orders.iterator();
    }

    public static class Order implements Serializable
    {
        private Direction direction;
        private String property;

        public Order(Direction direction, String property)
        {
            this.direction = direction;
            this.property = property;
        }

        public Direction getDirection()
        {
            return direction;
        }

        public String getProperty()
        {
            return property;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((direction == null) ? 0 : direction.hashCode());
            result = prime * result + ((property == null) ? 0 : property.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Order other = (Order) obj;
            if (direction != other.direction)
                return false;
            if (property == null)
            {
                if (other.property != null)
                    return false;
            }
            else if (!property.equals(other.property))
                return false;
            return true;
        }

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orders == null) ? 0 : orders.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Sort other = (Sort) obj;
        if (orders == null)
        {
            if (other.orders != null)
                return false;
        }
        else if (!orders.equals(other.orders))
            return false;
        return true;
    }

}
