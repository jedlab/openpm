package com.jedlab.pm.webflow;

import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;

import com.jedlab.framework.db.QueryMapper;
import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.spring.service.Restriction;
import com.jedlab.framework.spring.web.Filter;
import com.jedlab.framework.web.AbstractQueryActionBean;
import com.jedlab.pm.model.Project;
import com.jedlab.pm.service.ProjectService;

/**
 * @author Omid Pourhadi
 *
 */
public class ProjectQueryWebFlow extends AbstractQueryActionBean<Project>
{

    @Autowired
    transient ProjectService projectService;

    private ProjectFilter filter = new ProjectFilter();

    public ProjectFilter getFilter()
    {
        return filter;
    }

    @Override
    public AbstractCrudService<Project> getService()
    {
        return projectService;
    }

    @Override
    protected Restriction getRestriction()
    {
        return criteria -> {
            criteria.createAlias("owner", "o", JoinType.LEFT_OUTER_JOIN);
            QueryMapper.filterMap(getFilter(), criteria);
        };
    }

}
