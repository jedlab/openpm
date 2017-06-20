package com.jedlab.pm.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;

public class JsfRedirectStrategy implements RedirectStrategy
{

    protected final Log logger = LogFactory.getLog(getClass());

    private boolean contextRelative;

    /**
     * Redirects the response to the supplied URL.
     * <p>
     * If <tt>contextRelative</tt> is set, the redirect value will be the value
     * after the request context path. Note that this will result in the loss of
     * protocol information (HTTP or HTTPS), so will cause problems if a
     * redirect is being performed to change to HTTPS, for example.
     */
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException
    {
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
        redirectUrl = response.encodeRedirectURL(redirectUrl);

        if (logger.isDebugEnabled())
        {
            logger.debug("Redirecting to '" + redirectUrl + "'");
        }

        // we should redirect using ajax response if the case warrants
        boolean ajaxRedirect = request.getHeader("faces-request") != null
                && request.getHeader("faces-request").toLowerCase().indexOf("ajax") > -1;

        if (ajaxRedirect)
        {
            // javax.faces.context.FacesContext ctxt =
            // javax.faces.context.FacesContext.getCurrentInstance();
            // ctxt.getExternalContext().redirect(redirectUrl);

            String ajaxRedirectXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<partial-response><redirect url=\"" + redirectUrl
                    + "\"></redirect></partial-response>";
            response.setContentType("text/xml");
            response.getWriter().write(ajaxRedirectXml);
        }
        else
        {
            response.sendRedirect(redirectUrl);
        }

    }

    private String calculateRedirectUrl(String contextPath, String url)
    {
        if (!UrlUtils.isAbsoluteUrl(url))
        {
            if (contextRelative)
            {
                return url;
            }
            else
            {
                return contextPath + url;
            }
        }

        // Full URL, including http(s)://

        if (!contextRelative)
        {
            return url;
        }

        // Calculate the relative URL from the fully qualified URL, minus the
        // scheme and base context.
        url = url.substring(url.indexOf("://") + 3); // strip off scheme
        url = url.substring(url.indexOf(contextPath) + contextPath.length());

        if (url.length() > 1 && url.charAt(0) == '/')
        {
            url = url.substring(1);
        }

        return url;
    }

    /**
     * If <tt>true</tt>, causes any redirection URLs to be calculated minus the
     * protocol and context path (defaults to <tt>false</tt>).
     */
    public void setContextRelative(boolean useRelativeContext)
    {
        this.contextRelative = useRelativeContext;
    }
}
