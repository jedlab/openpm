package com.jedlab.framework.web;


import java.io.Serializable;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

public interface DialogManager<T> extends Serializable
{

    public void close(SelectEvent event);

    public void open();

    default public void select(T model)
    {
        RequestContext.getCurrentInstance().closeDialog(model);
    }

}
