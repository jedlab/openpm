package com.jedlab.framework.spring.mvc;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [DOT] com
 * 
 */
public class EntityWrapper<T>
{

    private T jsonEntity;
    private T persistedEntity;

    public EntityWrapper(T jsonEntity, T persistedEntity)
    {
        this.jsonEntity = jsonEntity;
        this.persistedEntity = persistedEntity;
    }

    public T getJsonEntity()
    {
        return jsonEntity;
    }

    public void setJsonEntity(T jsonEntity)
    {
        this.jsonEntity = jsonEntity;
    }

    public T getPersistedEntity()
    {
        return persistedEntity;
    }

    public void setPersistedEntity(T persistedEntity)
    {
        this.persistedEntity = persistedEntity;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jsonEntity == null) ? 0 : jsonEntity.hashCode());
        result = prime * result + ((persistedEntity == null) ? 0 : persistedEntity.hashCode());
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
        EntityWrapper other = (EntityWrapper) obj;
        if (jsonEntity == null)
        {
            if (other.jsonEntity != null)
                return false;
        }
        else if (!jsonEntity.equals(other.jsonEntity))
            return false;
        if (persistedEntity == null)
        {
            if (other.persistedEntity != null)
                return false;
        }
        else if (!persistedEntity.equals(other.persistedEntity))
            return false;
        return true;
    }

}
