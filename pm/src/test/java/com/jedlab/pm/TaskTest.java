package com.jedlab.pm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jedlab.pm.model.Project;
import com.jedlab.pm.model.Task;
import com.jedlab.pm.service.ProjectService;
import com.jedlab.pm.service.TaskService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-context.xml"})
public class TaskTest
{

    @Autowired
    private TaskService taskService;
    
    @Autowired
    ProjectService projectService;
    
    @Test
    public void taskInsertProject()
    {
        Project project = projectService.findAll().iterator().next();
        for (int i = 0; i < 10; i++)
        {
            Task t = new Task();
            t.setProject(project);
            t.setName("task " + i);
            taskService.persist(t);            
        }
    }
    
}
