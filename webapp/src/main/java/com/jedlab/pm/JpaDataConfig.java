package com.jedlab.pm;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * @author Omid Pourhadi
 *
 */
@Configuration
@EntityScan({ "com.jedlab.pm.model" })
@EnableJpaRepositories({ "com.jedlab.pm.dao" })
@EnableJpaAuditing
public class JpaDataConfig
{

  

}
