package com.jedlab.pm.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jedlab.framework.exceptions.ServiceException;
import com.jedlab.framework.spring.dao.AbstractDAO;
import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.pm.dao.TaskDao;
import com.jedlab.pm.model.Task;

@Transactional
@Service
public class TaskService extends AbstractCrudService<Task>
{

    @Autowired
    TaskDao taskDao;
    
    @Autowired
    ProjectService projectService;
    
    @Autowired
    MessageSource messageSource;
    
    
    public void persist(Task instance)
    {
        taskDao.save(instance);
    }

    @Override
    public AbstractDAO<Task> getDao()
    {
        
        return (AbstractDAO<Task>) taskDao;
    }
    
    @Override
    protected void beforeInsert(Task entity) {
        if(entity.getProject()== null)
            throw new ServiceException(messageSource.getMessage("No Project", null, Locale.getDefault()));
    }
    
    @Override
    protected void afterInsert(Task entity) {
        
    }
    
    
    
}
