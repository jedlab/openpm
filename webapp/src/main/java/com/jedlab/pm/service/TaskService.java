package com.jedlab.pm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jedlab.pm.dao.TaskDao;
import com.jedlab.pm.model.Task;

@Service
public class TaskService
{

    @Autowired
    TaskDao taskDao;
    
    @Autowired
    ProjectService projectService;
    
    @Transactional
    public void persist(Task instance)
    {
        taskDao.save(instance);
    }
    
}
