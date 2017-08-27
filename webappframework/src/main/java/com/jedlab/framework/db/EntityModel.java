package com.jedlab.framework.db;

import java.io.Serializable;

/**
 * @author Omid Pourhadi
 *
 */
public interface EntityModel<ID extends Serializable> extends Serializable
{

    public ID getId();
    
//    public void setId(ID id);
    
}
