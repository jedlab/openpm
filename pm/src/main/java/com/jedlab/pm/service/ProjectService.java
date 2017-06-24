package com.jedlab.pm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jedlab.framework.exceptions.ServiceException;
import com.jedlab.framework.spring.dao.AbstractDAO;
import com.jedlab.framework.spring.security.AuthenticationUtil;
import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.pm.dao.ProjectDao;
import com.jedlab.pm.dao.UserDao;
import com.jedlab.pm.model.Project;
import com.jedlab.pm.model.User;

@Service
@Transactional
public class ProjectService extends AbstractCrudService<Project>
{

    @Autowired
    ProjectDao projectDao;
    
    @Autowired
    UserDao userDao;

    @Override
    public AbstractDAO<Project> getDao()
    {
        return projectDao;
    }
    
    @Override
    protected void beforeInsert(Project entity)
    {
        if(AuthenticationUtil.isLoggedIn() == false)
            throw new ServiceException("user is not loggedin");
        User u = userDao.findOne(AuthenticationUtil.getUserId());
        entity.setOwner(u);
    }
    
}
