package com.jedlab.framework.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

public abstract class DefaultDialog<T> implements DialogManager<T>
{

    private String outcome;

    public DefaultDialog(String outcome)
    {
        this.outcome = outcome;
    }

    @Override
    public void close(SelectEvent event)
    {
        afterClose((T) event.getObject());
    }

    protected abstract void afterClose(T object);
    public abstract void clear();

    @Override
    public void open()
    {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("width", "80%");
        options.put("contentWidth", "100%");
        options.put("height", "750");
        options.put("contentHeight", "85%");
        options.put("modal", true);
        options.put("closable", true);
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("from", Arrays.asList("fromView"));
        RequestContext.getCurrentInstance().openDialog(outcome, options, params);
    }

}
