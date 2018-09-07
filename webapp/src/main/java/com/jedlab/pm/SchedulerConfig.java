package com.jedlab.pm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

/**
 *
 * @author Omid Pourhadi
 *
 */
@Configuration
@EnableAsync
@EnableScheduling
public class SchedulerConfig
{

    @Bean
    public TaskScheduler taskScheduler()
    {
        return new ConcurrentTaskScheduler();
    }

}
