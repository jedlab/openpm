package com.jedlab.pm.dao;

import com.jedlab.pm.model.Project;

/**
 * @author omidp
 *
 */
public interface ProjectDaoCustom
{

    
    /**
     * check duplication in edit mode
     * @param projectName
     * @return
     */
    public Project findProjectByName(String projectName);
    
    /**
     * check duplicatoin in update mode
     * @param name
     * @param id
     * @return
     */
    public Project findProjectByNameAndId(String name, Long id);
    
}
