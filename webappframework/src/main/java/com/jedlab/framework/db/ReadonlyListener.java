package com.jedlab.framework.db;

import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

/**
 * @author Omid Pourhadi
 *
 */
public class ReadonlyListener implements PreInsertEventListener, PreUpdateEventListener, PreDeleteEventListener
{

    @Override
    public boolean onPreInsert(PreInsertEvent event)
    {
        Class<? extends Object> entityClass = event.getEntity().getClass();
        if (entityClass.isAnnotationPresent(Readonly.class))
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event)
    {
        Class<? extends Object> entityClass = event.getEntity().getClass();
        if (entityClass.isAnnotationPresent(Readonly.class))
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event)
    {
        Class<? extends Object> entityClass = event.getEntity().getClass();
        if (entityClass.isAnnotationPresent(Readonly.class))
        {
            return true;
        }
        return false;
    }

}
