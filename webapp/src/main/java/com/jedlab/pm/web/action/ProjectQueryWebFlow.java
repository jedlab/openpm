package com.jedlab.pm.web.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import com.jedlab.framework.report.ReportHeader;
import com.jedlab.framework.spring.security.AuthenticationUtil;
import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.spring.service.JPARestriction;
import com.jedlab.framework.web.AbstractQueryActionBean;
import com.jedlab.pm.filters.ProjectFilter;
import com.jedlab.pm.model.Project;
import com.jedlab.pm.report.DefaultReportHeader;
import com.jedlab.pm.service.ProjectService;

import ar.com.fdvs.dj.domain.builders.FastReportBuilder;

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
    protected JPARestriction getRestriction()
    {
        return new ProjectRestriction();
    }

    public void resetSearch()
    {
        this.filter = new ProjectFilter();
    }
    
    private class ProjectRestriction implements JPARestriction
    {

        @Override
        public Specification countSpec(CriteriaBuilder builder, CriteriaQuery criteria, Root root)
        {
            return (rootEntity, query, criteriaBuilder) -> {
                rootEntity.join("owner", JoinType.LEFT);
                return applyFilter(rootEntity, query, criteriaBuilder);
            };
        }

        @Override
        public Specification listSpec(CriteriaBuilder builder, CriteriaQuery criteria, Root root)
        {
            return (rootEntity, query, criteriaBuilder) -> {
                Fetch<Object, Object> fetch = rootEntity.fetch("owner", JoinType.LEFT);
                return applyFilter(rootEntity, query, criteriaBuilder);
            };
        }
        private Predicate applyFilter(Root rootEntity, CriteriaQuery query, CriteriaBuilder criteriaBuilder)
        {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            predicateList.add(criteriaBuilder.equal(rootEntity.get("owner").get("id"), AuthenticationUtil.getUserId()));
            return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        }
            
    }
    
    @Override
    protected ReportSection getReportSection()
    {
        return new ReportSection() {
            
            @Override
            public Map<String, Object> getParameters()
            {
                return null;
            }
            
            @Override
            public ReportHeader getFormReportHeader()
            {
                return new DefaultReportHeader("Project");
            }
            
            @Override
            public void beforeFormGenerateReport(FastReportBuilder frb)
            {
                
            }
        };
    }

}
