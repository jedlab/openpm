package com.jedlab.pm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

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
       for (int i = 4; i < 30; i++)
    {
           Task task=new Task();
           task.setProject(projectService.findAll().iterator().next());
           task.setName("task "+i);
           taskService.insert(task);
           
    }
    }
    
}
