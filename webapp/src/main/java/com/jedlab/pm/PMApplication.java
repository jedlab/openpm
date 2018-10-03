package com.jedlab.pm;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.webapp.FacesServlet;
import javax.servlet.DispatcherType;

import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.ocpsoft.rewrite.servlet.impl.RewriteServletContextListener;
import org.ocpsoft.rewrite.servlet.impl.RewriteServletRequestListener;
import org.primefaces.webapp.filter.FileUploadFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.jedlab.framework.web.FarsiLocaleFilter;
import com.jedlab.framework.web.FarsiTypeFilter;


@Configuration
@ComponentScan
@EnableAutoConfiguration(excludeName = { "org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration" }, exclude = {
        JacksonAutoConfiguration.class })
@EnableConfigurationProperties
@PropertySource({ "classpath:database.properties" })
//@Import(ContainerConfig.class)
@ImportResource(locations = { "classpath*:framework-spring.xml" })
public class PMApplication extends SpringBootServletInitializer implements WebApplicationInitializer
{
    
    private static final Logger LOGGER = Logger.getLogger(PMApplication.class.getName());
    
    @Autowired
    ConfigurableApplicationContext context;
    
    public static void main(String[] args)
    {
        SpringApplication sp = new SpringApplication(PMApplication.class);
        sp.setBanner(new Banner() {

            @Override
            public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out)
            {
                out.println("Portal");
            }
        });
        sp.run(args);

    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources(PMApplication.class, Initializer.class);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx)
    {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames)
            {
//                 System.out.println(beanName);
            }

        };
    }

    @Bean
    public RewriteServletRequestListener rewriteServletRequestListener()
    {
        return new RewriteServletRequestListener();
    }

    @Bean
    public RewriteServletContextListener rewriteServletContextListener()
    {
        return new RewriteServletContextListener();
    }

    @Bean
    public FilterRegistrationBean prettyFilter()
    {
        FilterRegistrationBean prettyFilter = new FilterRegistrationBean(new RewriteFilter());
        prettyFilter.setFilter(new RewriteFilter());
        prettyFilter.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
        prettyFilter.addUrlPatterns("/*");
        return prettyFilter;
    }

    @Bean
    public FilterRegistrationBean characterEncodingFilter()
    {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CharacterEncodingFilter("UTF-8", true, true));
        registration.addUrlPatterns("/*");
        registration.setName("characterEncodingFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean fileUploadFilter()
    {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new FileUploadFilter());
        registration.setServletNames(Arrays.asList("Faces Servlet"));
        registration.setName("fileUploadFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean farsiTypeFilter()
    {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new FarsiTypeFilter());
        registration.addUrlPatterns("/*");
        registration.setName("farsiTypeFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean farsiLocaleFilter()
    {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new FarsiLocaleFilter());
        registration.addUrlPatterns("/*");
        registration.setName("farsiLocaleFilter");
        registration.addInitParameter("locale", "fa");
        return registration;
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean()
    {
        FacesServlet servlet = new FacesServlet();
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(servlet, "*.xhtml");
        servletRegistrationBean.setName("Faces Servlet");
        servletRegistrationBean.setLoadOnStartup(1);
        return servletRegistrationBean;
    }

    
    
    @PostConstruct
    protected void initShutdownHook() {
        context.registerShutdownHook();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                LOGGER.info("appContext.close()");
                context.close();
            }
        });
    }

}
