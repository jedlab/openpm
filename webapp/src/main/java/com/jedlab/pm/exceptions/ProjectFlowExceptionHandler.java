package com.jedlab.pm.exceptions;

import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.execution.FlowExecutionException;

/**
 * @author omidp
 *
 */
public class ProjectFlowExceptionHandler extends SimpleMappingExceptionResolver implements FlowExecutionExceptionHandler
{

    public static final String FLOW_EXECUTION_EXCEPTION_ATTRIBUTE = "flowExecutionException";
    
    @Override
    public boolean canHandle(FlowExecutionException exception)
    {
        return true;
    }

    @Override
    public void handle(FlowExecutionException exception, RequestControlContext context)
    {
        context.getFlashScope().put(FLOW_EXECUTION_EXCEPTION_ATTRIBUTE, exception);
        context.execute(new Transition(new DefaultTargetStateResolver("error")));
    }

}
