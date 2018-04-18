package com.jedlab.framework.json.xml;

import java.util.Set;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonsConfig
{

    
    private static Logger log = LoggerFactory.getLogger(CommonsConfig.class);

    private static CommonsConfig config;
    
    private static final String COMMONS_CONFIG_HOME = "commons-config.xml";

    private CombinedConfiguration combinedConfiguration;

    private CommonsConfig()
    {
    }

    public static synchronized CommonsConfig getInstance()
    {
        if (config == null)
        {
            config = new CommonsConfig();
        }
        return config;
    }

    public CombinedConfiguration getCombinedConfig()
    {
        if (combinedConfiguration == null)
        {
            try
            {
                log.info("loading configurations from " + COMMONS_CONFIG_HOME);
                DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder(COMMONS_CONFIG_HOME);
                combinedConfiguration = builder.getConfiguration(true);
                Set<String> names = combinedConfiguration.getConfigurationNames();
                if (names.contains("sys"))
                {
                    log.info("adding SystemConfiguration as 'sys'");
                    combinedConfiguration.addConfiguration(new SystemConfiguration(), "sys");
                }
                if (names.contains("env"))
                {
                    log.info("adding EnvironmentConfiguration as 'env'");
                    combinedConfiguration.addConfiguration(new EnvironmentConfiguration(), "env");
                }
                for (Configuration c : combinedConfiguration.getConfigurations())
                {
                    if (c instanceof FileConfiguration)
                    {
                        log.info("setting reloading strategy to FileChangedReloadingStrategy for " + c);
                        FileConfiguration fc = (FileConfiguration) c;
                        fc.setReloadingStrategy(new FileChangedReloadingStrategy());
                    }
                }
            }
            catch (ConfigurationException ex)
            {
                log.error("could not load configuration", ex);
            }
        }

        return combinedConfiguration;
    }
    
}
