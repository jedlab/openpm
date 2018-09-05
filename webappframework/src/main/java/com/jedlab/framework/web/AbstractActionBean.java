package com.jedlab.framework.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

import com.jedlab.framework.report.JasperDataExporter.Exporter;

public abstract class AbstractActionBean implements Serializable
{

    private static final Logger logger = Logger.getLogger(AbstractActionBean.class.getName());

    public Map<String, String> getRequestParameterMap()
    {
        return getCurrentInstance().getExternalContext().getRequestParameterMap();
    }
    
    public Map<String, String[]> getRequestParameterValuesMap()
    {
        return getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
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
    
    protected OutputStream outputstream(Exporter e) throws IOException
    {
        FacesContext context = getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        response.setContentType(e.getContentType());
        response.setCharacterEncoding("UTF-8");
        //
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        if (isInternetExplorer(request)) response.setHeader("Content-Disposition",
                e.getContentDisposition() + "; filename=\"" + URLEncoder.encode(e.getFileName(), "utf-8") + "\"");
        else response.setHeader("Content-Disposition",
                e.getContentDisposition() + "; filename=\"" + MimeUtility.encodeWord(e.getFileName(), "UTF-8", "Q") + "\"");
        //
        OutputStream os = response.getOutputStream();
        return os;
    }
    
    protected boolean isInternetExplorer(HttpServletRequest request)
    {
        String userAgent = request.getHeader("user-agent");
        return (userAgent.indexOf("MSIE") > -1);
    }
    
    
    public void closeDialog()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }
    
    

    protected static HttpServletRequest getRequestUrl(FacesContext facesContext)
    {
       Object request = facesContext.getExternalContext().getRequest(); 
       if (request instanceof HttpServletRequest) 
       {
          return ( (HttpServletRequest) request);
       }
       else
       {
          return null;
       }
    }

}
