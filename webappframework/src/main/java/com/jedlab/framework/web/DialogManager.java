package com.jedlab.framework.web;


import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

public interface DialogManager<T>
{

    public void close(SelectEvent event);

    public void open();

    default public void select(T model)
    {
        RequestContext.getCurrentInstance().closeDialog(model);
    }

}
