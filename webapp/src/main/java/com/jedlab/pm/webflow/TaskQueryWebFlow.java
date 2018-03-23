package com.jedlab.pm.webflow;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;

import com.jedlab.framework.db.QueryMapper;
import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.spring.service.Restriction;
import com.jedlab.framework.spring.web.Filter;
import com.jedlab.framework.spring.web.ParamOperator;
import com.jedlab.framework.spring.web.QParam;
import com.jedlab.framework.web.AbstractQueryActionBean;
import com.jedlab.pm.model.Task;
import com.jedlab.pm.service.TaskService;

public class TaskQueryWebFlow extends AbstractQueryActionBean<Task>
{

    @Autowired
    transient TaskService taskService;
    
    
    private TaskFilter filter= new TaskFilter();
    
    
    public TaskFilter getFilter()
    {
        return filter;
    }


    @Override
    public AbstractCrudService<Task> getService()
    {
        
        return taskService;
    }
    
    
    @Override
    protected Restriction getRestriction()
    {
       
        return criteria -> {
           
            criteria.createCriteria("project", "p", JoinType.LEFT_OUTER_JOIN);
            criteria.createCriteria("p.owner", "o", JoinType.LEFT_OUTER_JOIN);
            QueryMapper.filterMap(getFilter(), criteria);
            
            
            
            /*
            if(getFilter().getTaskName() != null)
                criteria.add(Restrictions.like("name", getFilter().getTaskName(),MatchMode.ANYWHERE));
                */
        };
        
        /*
        
        return new Restriction() {
                    
                    @Override
                    public void applyFilter(Criteria criteria)
                    {
                       
                        criteria.createCriteria("project", "p", JoinType.LEFT_OUTER_JOIN);
                        criteria.createCriteria("p.owner", "o", JoinType.LEFT_OUTER_JOIN);
                    }
                };
                */
    }
    
    
    public void resetSearch() {
        filter =new TaskFilter();
    }
    
    public static class TaskFilter implements Filter{
        
        private String taskName;
        
        private String username;

        @QParam(operator = ParamOperator.LIKE, propertyName="name")
        public String getTaskName()
        {
            return taskName;
        }

        public void setTaskName(String taskName)
        {
            this.taskName = taskName;
        }

        @QParam(operator = ParamOperator.EQ, propertyName="o.username")
        public String getUsername()
        {
            return username;
        }

        public void setUsername(String username)
        {
            this.username = username;
        }
        
        
        
        
    }

}
