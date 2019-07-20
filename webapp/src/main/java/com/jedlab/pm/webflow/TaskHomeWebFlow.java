package com.jedlab.pm.webflow;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import com.google.common.collect.Lists;
import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.web.AbstractHomeActionBean;
import com.jedlab.pm.model.Project;
import com.jedlab.pm.model.Task;
import com.jedlab.pm.service.ProjectService;
import com.jedlab.pm.service.TaskService;

public class TaskHomeWebFlow extends AbstractHomeActionBean<Task>
{

    @Autowired
    transient TaskService taskService;
    
    @Autowired
    transient ProjectService projectService;
    
    

    public Long getTaskId()
    {
        return (Long) getId();
    }

    public void setTaskId(Long taskId)
    {
        setId(taskId);
    }

    @Override
    public AbstractCrudService<Task> getService()
    {
        
        return taskService;
    }
    
    public void onItemSelect(SelectEvent event)
    {
        Project p=new Project();
        p.setId((Long) event.getObject());
        
        getInstance().setProject(p);
        
        
    }
    
    
    public List<Project> completeProject(String pname)
    {
        List<Project> projects=new ArrayList<Project>();
        
        Iterable<Project> findAll = projectService.findAll(new ProjectSpecification(pname));
        projects.addAll(Lists.newArrayList(findAll));
        return projects;
    }
    
    
    public static class ProjectSpecification implements Specification<Project>
    {

        private String projectName;
        
        
        public ProjectSpecification(String projectName)
        {
            this.projectName = projectName;
        }


        @Override
        public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder cb)
        {
           
            return cb.like(root.get("name"),"%"+projectName+"%" );
        }
        
    }
}
