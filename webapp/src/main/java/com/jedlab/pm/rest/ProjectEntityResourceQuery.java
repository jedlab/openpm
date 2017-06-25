package com.jedlab.pm.rest;

import javax.ws.rs.Path;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jedlab.pm.model.Project;

/**
 * @author omidp
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProjectEntityResourceQuery extends EntityResourceQuery<Project>
{

}
