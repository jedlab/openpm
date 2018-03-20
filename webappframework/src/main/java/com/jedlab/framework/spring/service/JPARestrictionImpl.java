package com.jedlab.framework.spring.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import com.jedlab.framework.util.CollectionUtil;

public class JPARestrictionImpl implements JPARestriction
{

    private List<Join> joinProperties;
    private Restriction restriction;

    public JPARestrictionImpl(Restriction restriction)
    {
        joinProperties = new ArrayList<>();
        this.restriction = restriction;
    }

    public JPARestrictionImpl join(Join join)
    {
        joinProperties.add(join);
        return this;
    }

    @Override
    public void applyFilter(CriteriaBuilder builder, CriteriaQuery criteria, Root root, boolean joinFetch)
    {
        if (CollectionUtil.isNotEmpty(joinProperties))
        {
            joinProperties.forEach(join -> {
                if (joinFetch)
                    root.fetch(join.getPropertyName(), join.getJoinType());
                else
                    root.join(join.getPropertyName(), join.getJoinType());
            });
        }
        this.restriction.applyFilter(builder, criteria, root);
    }

    public static class Join
    {
        private String propertyName;
        private JoinType joinType;
        
        public static Join of(String propertyName)
        {
            return new Join(propertyName);
        }
        
        public static Join of(String propertyName, JoinType joinType)
        {
            return new Join(propertyName, joinType);
        }

        public Join(String propertyName)
        {
            this.propertyName = propertyName;
            this.joinType = JoinType.LEFT;
        }

        public Join(String propertyName, JoinType joinType)
        {
            this.propertyName = propertyName;
            this.joinType = joinType;
        }

        public String getPropertyName()
        {
            return propertyName;
        }

        public JoinType getJoinType()
        {
            return joinType;
        }

    }

}
