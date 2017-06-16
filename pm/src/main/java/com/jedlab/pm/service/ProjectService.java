package com.jedlab.pm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jedlab.pm.dao.ProjectDao;
import com.jedlab.pm.model.Project;

@Service
public class ProjectService
{

    @Autowired
    ProjectDao projectDao;
    
    @Transactional
    public void persist(Project instance)
    {
        //begin 
        projectDao.save(instance);
        //end
    }
    
    public Iterable<Project> findAll()
    {
        Iterable<Project> result = projectDao.findAll();
        return result;
    }
    
    
}
