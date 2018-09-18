package com.jedlab.pm;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import org.atmosphere.cpr.ApplicationConfig;
import org.primefaces.push.PushServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.jedlab.framework.spring.security.SecurityHandlerInterceptor;

/**
 *
 * @author Omid Pourhadi
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converters.add(new FormHttpMessageConverter());
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new BufferedImageHttpMessageConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
        localeInterceptor.setParamName("lang");
        registry.addInterceptor(localeInterceptor);
        registry.addInterceptor(new SecurityHandlerInterceptor());
    }

    @Bean
    public LocaleResolver localeResolver()
    {
        FixedLocaleResolver localeResolver = new FixedLocaleResolver(new Locale("fa", "IR"));
        localeResolver.setDefaultLocale(new Locale("fa", "IR"));
        return localeResolver;
    }

   

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

   

    @Bean
    public ServletRegistrationBean pushServlet()
    {
        PushServlet ps = new PushServlet();
        ServletRegistrationBean bean = new ServletRegistrationBean(ps, "/primepush/*");
        bean.setAsyncSupported(true);
        bean.setName("Push Servlet");
        bean.addInitParameter("org.atmosphere.annotation.packages", "org.primefaces.push");
        bean.addInitParameter("org.atmosphere.cpr.packages", "com.jedlab.pm.websocket");
        bean.addInitParameter(ApplicationConfig.ANALYTICS, "false");
        bean.addInitParameter(ApplicationConfig.BROADCASTER_CACHE, "org.atmosphere.cache.UUIDBroadcasterCache");
        bean.addInitParameter(ApplicationConfig.BROADCASTER_CLASS, "org.atmosphere.plugin.redis.RedisBroadcaster");
        bean.addInitParameter("com.cloudst.cfs.ws.RedisBroadcaster.sharedPool", "true");
        bean.addInitParameter("org.atmosphere.cpr.broadcaster.shareableThreadPool", "true");
        bean.addInitParameter("org.atmosphere.cpr.sessionSupport", "false");
        bean.setLoadOnStartup(0);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

  


}
