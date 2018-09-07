package com.jedlab.pm.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.jedlab.pm.dao.ProjectDao;
import com.jedlab.pm.model.Project;

@Component
public class ProjectValidator implements Validator
{

    private static final Logger logger = LoggerFactory.getLogger(ProjectValidator.class);
    
    @Autowired
    ProjectDao projectDao;
    
  

    @Override
    public boolean supports(Class<?> clazz)
    {
        return clazz.isAssignableFrom(Project.class);
    }

    @Override
    public void validate(Object target, Errors errors)
    {
        logger.info("validating project");
        Project project = (Project) target;
        Project result = null;
        if(project.isNew())
             result = projectDao.findProjectByName(project.getName());
        else
            result = projectDao.findProjectByNameAndId(project.getName(), project.getId());
        if(result != null)
        {
            errors.rejectValue("name", "duplicate");
        }
        
    }
    
    
}
