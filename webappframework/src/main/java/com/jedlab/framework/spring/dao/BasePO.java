package com.jedlab.framework.spring.dao;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.jedlab.framework.db.EntityModel;

/**
 * field accessor entities
 * @author omidp
 *
 */
@MappedSuperclass
public abstract class BasePO implements EntityModel<Long>
{

    @Id
    @GeneratedValue
    private Long id;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @Transient
    // DATAJPA-622
    public boolean isNew()
    {
        return null == getId();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BasePO other = (BasePO) obj;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        return true;
    }

}
