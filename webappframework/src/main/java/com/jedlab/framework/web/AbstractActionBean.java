package com.jedlab.framework.web;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;

public abstract class AbstractActionBean implements Serializable
{

    private static final Logger logger = Logger.getLogger(AbstractActionBean.class.getName());

    public Map<String, String> getRequestParameterMap()
    {
        return getCurrentInstance().getExternalContext().getRequestParameterMap();
    }

    public String getCurrentViewID()
    {
        String viewId = getCurrentInstance().getViewRoot().getViewId();
        return viewId.substring(viewId.lastIndexOf('/') + 1);
    }

    public Locale getCurrentLocale()
    {
        FacesContext facesContext = getCurrentInstance();
        // first get locale from UIViewRoot.
        Locale requestLocale = facesContext.getViewRoot().getLocale();
        if (requestLocale == null)
        {
            return facesContext.getApplication().getDefaultLocale();
        }
        else
        {
            return requestLocale;
        }
    }

    protected FacesContext getCurrentInstance()
    {
        return FacesContext.getCurrentInstance();
    }
    
    public boolean isPostback()
    {
        return FacesContext.getCurrentInstance().isPostback();
    }
    
    public void resetPagination(String compId)
    {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(compId);
        if(dataTable != null)
            dataTable.reset();
    }

}
