package com.jedlab.pm.rest;

import javax.ws.rs.Path;

import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jedlab.framework.db.QueryMapper;
import com.jedlab.framework.spring.security.AuthenticationUtil;
import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.spring.service.Restriction;
import com.jedlab.pm.model.Task;
import com.jedlab.pm.service.TaskService;

@Component
@Path("/task")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskResourceQuery extends ResourceQuery<Task>
{

    @Autowired
    TaskService taskService;
    
    @Autowired
    TaskEntityResourceQuery subResourceLocator;
    
    
    
    @Override
    protected AbstractCrudService<Task> getService()
    {
       
        return taskService;
    }

    @Override
    public EntityResourceQuery<Task> getEntityResourceQuery()
    {
       
        return subResourceLocator;
    }
    
    @Override
    protected Restriction getRestriction()
    {
        
        return criteria -> {
            
            criteria.createCriteria("project", "p", JoinType.LEFT_OUTER_JOIN);
            criteria.createCriteria("p.owner", "o", JoinType.LEFT_OUTER_JOIN);
            if(AuthenticationUtil.getUserId() != null)
                criteria.add(Restrictions.eq("o.id", AuthenticationUtil.getUserId()));
           
        };
    }

}
