package com.jedlab.pm.rest;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import com.jedlab.pm.model.Task;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskEntityResourceQuery extends EntityResourceQuery<Task>
{

}
