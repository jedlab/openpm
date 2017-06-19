package com.jedlab.framework.web;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;

/**
 * @author OmidPourhadi [AT] gmail [DOT] com
 *
 */
public class FarsiLocaleFilter implements javax.servlet.Filter
{

    private static final String DEFAULT_LOCALE_NAME = "locale";
    private String localeName;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        localeName = filterConfig.getInitParameter(DEFAULT_LOCALE_NAME);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        if (localeName != null)
            LocaleContextHolder.setLocaleContext(new SimpleLocaleContext(new Locale(localeName)));
        chain.doFilter(request, response);
    }

    @Override
    public void destroy()
    {

    }

}
