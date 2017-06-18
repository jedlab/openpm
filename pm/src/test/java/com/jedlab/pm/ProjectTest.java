package com.jedlab.pm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jedlab.pm.model.Project;
import com.jedlab.pm.service.ProjectService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-context.xml"})
public class ProjectTest
{

    @Autowired
    private ProjectService projectService;
    
    @Test
    public void projectInsertProject()
    {
        Project p = new Project();
        p.setName("my proj");
        projectService.insert(p);
    }
    
}
