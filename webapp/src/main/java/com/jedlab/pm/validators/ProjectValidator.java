package com.jedlab.pm.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.stereotype.Component;

import com.jedlab.pm.dao.ProjectDao;
import com.jedlab.pm.model.Project;

@Component
public class ProjectValidator
{

    private static final Logger logger = LoggerFactory.getLogger(ProjectValidator.class);
    
    @Autowired
    ProjectDao projectDao;
    
    public void validateProjectEdit(Project project, ValidationContext vc)
    {
        logger.info("validating project");
        MessageContext messages = vc.getMessageContext();
        Project result = null;
        if(project.isNew())
             result = projectDao.findProjectByName(project.getName());
        else
            result = projectDao.findProjectByNameAndId(project.getName(), project.getId());
        if(result != null)
        {
            messages.addMessage(new MessageBuilder().error().source("Project")
                    .code("Project_Exists").build());
        }
    }
    
    
}
