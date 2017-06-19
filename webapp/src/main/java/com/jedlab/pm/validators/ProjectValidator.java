package com.jedlab.pm.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.stereotype.Component;

import com.jedlab.pm.model.Project;

@Component
public class ProjectValidator
{

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    public void validateProjectEdit(Project project, ValidationContext vc)
    {
        logger.info("validating project");
    }
    
    
}
