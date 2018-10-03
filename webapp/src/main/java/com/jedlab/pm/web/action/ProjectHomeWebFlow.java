package com.jedlab.pm.web.action;

import org.springframework.beans.factory.annotation.Autowired;

import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.web.AbstractHomeActionBean;
import com.jedlab.pm.model.Project;
import com.jedlab.pm.service.ProjectService;

public class ProjectHomeWebFlow extends AbstractHomeActionBean<Project>
{

    @Autowired
    transient ProjectService projectService;


    public Long getProjectId()
    {
        return (Long) getId();
    }

    public void setProjectId(Long projectId)
    {
        setId(projectId);
    }

    @Override
    public AbstractCrudService<Project> getService()
    {
        return projectService;
    }

}
