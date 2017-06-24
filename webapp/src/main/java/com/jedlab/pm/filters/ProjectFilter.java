package com.jedlab.pm.filters;

import com.jedlab.framework.spring.web.Filter;
import com.jedlab.framework.spring.web.ParamOperator;
import com.jedlab.framework.spring.web.QParam;

/**
 * @author Omid Pourhadi
 *
 */
public class ProjectFilter implements Filter
{

    private String projectName;

    @QParam(operator = ParamOperator.LIKE, propertyName="name")
    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

}
