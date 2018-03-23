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
@TransactionConfiguration(defaultRollback=false,transactionManager="transactionManage")
public class TaskTest
{

    @Autowired
    private TaskService taskService;
    
    @Autowired
    ProjectService projectService;
    
   
    
    @Test
    @Transactional
    public void taskInsertProject()
    {
       
       
       
            Task t = new Task();
            t.setProject(projectService.findAll().iterator().next());
            t.setName("task 1");
            taskService.persist(t);            
        
    }
    
   
    
}
