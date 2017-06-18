package com.jedlab.pm.webflow;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.web.AbstractQueryActionBean;
import com.jedlab.pm.model.Project;
import com.jedlab.pm.service.ProjectService;

public class ProjectQueryWebFlow extends AbstractQueryActionBean<Project>
{

    @Autowired
    transient ProjectService projectService;
    
    @Override
    public AbstractCrudService<Project> getService()
    {
        return projectService;
    }

    @Override
    public Specification<Project> getQueryFilter(Map<String, Object> dataTableFilterMap)
    {
        return new ProjectSpecification();
    }
    
    public static class ProjectSpecification implements Specification<Project>
    {

        @Override
        public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder cb)
        {
            root.join("owner", JoinType.LEFT);
            return null;
        }

       
        
    }


}
