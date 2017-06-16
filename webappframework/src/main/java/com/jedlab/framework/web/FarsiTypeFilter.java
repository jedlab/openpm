package com.jedlab.framework.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author OmidPourhadi [AT] gmail [DOT] com
 *
 */
public class FarsiTypeFilter implements javax.servlet.Filter
{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        if (request instanceof HttpServletRequest)
        {
            FarsiRequestWrapper frw = new FarsiRequestWrapper((HttpServletRequest) request);
            chain.doFilter(frw, response);
        }
        else
        {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy()
    {
        
    }

}
