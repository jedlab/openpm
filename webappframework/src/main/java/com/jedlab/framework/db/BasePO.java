package com.jedlab.framework.db;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.TableGenerator;

/**
 * getter accessor entities with table strategy
 * @author omidp
 *
 */
@MappedSuperclass
@Deprecated
public abstract class BasePO implements EntityModel<Long>
{

    private Long id;


    @GenericGenerator(name = "id_generator", strategy = "enhanced-table",
            parameters = { 
                @org.hibernate.annotations.Parameter(name = TableGenerator.TABLE_PARAM, value = "id_generator"),
                @org.hibernate.annotations.Parameter(name = TableGenerator.VALUE_COLUMN_PARAM, value = "gen_value"),
                @org.hibernate.annotations.Parameter(name = TableGenerator.SEGMENT_COLUMN_PARAM, value = "gen_name"),
                @org.hibernate.annotations.Parameter(name = TableGenerator.SEGMENT_VALUE_PARAM, value = "gen_entity"),
                @org.hibernate.annotations.Parameter(name = TableGenerator.INCREMENT_PARAM, value = "15")                
                })
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "id_generator")
    @Column(name = "id")
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BasePO other = (BasePO) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }
    
}
