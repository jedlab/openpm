package com.jedlab.pm.webflow;

import org.springframework.beans.factory.annotation.Autowired;

import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.web.AbstractHomeActionBean;
import com.jedlab.pm.model.Project;
import com.jedlab.pm.model.Task;
import com.jedlab.pm.service.TaskService;

public class TaskHomeWebFlow extends AbstractHomeActionBean<Task>
{
    
    @Autowired
    transient TaskService taskService;

    @Override
    public AbstractCrudService<Task> getService()
    {
        
        return taskService;
    }
    
    

}
