package com.jedlab.pm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
    {
        // auth.userDetailsService(udetailService()).passwordEncoder(passwordEncoder()).and().eraseCredentials(true).authenticationProvider(preAuth());
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    @Order(1)
    public static class FormWebSecurityConfig extends WebSecurityConfigurerAdapter
    {

        @Override
        public void configure(WebSecurity web) throws Exception
        {
            web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/resources/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception
        {
            http.csrf().disable().formLogin().loginProcessingUrl("/loginProcess").loginPage("/login").successForwardUrl("/home")
                    .failureUrl("/login?error=true").defaultSuccessUrl("/secure/dashboard", false).usernameParameter("username").and()
                    .authorizeRequests().antMatchers("/swagger*/**", "/resources/**", "/webjars/**", "/login").permitAll()
                    .antMatchers("/member*/**", "/secure*/**").authenticated().and().authorizeRequests().antMatchers("/api/v1/**")
                    .hasRole("API").and().httpBasic().and().headers().frameOptions().sameOrigin().and().anonymous().key("pm")
                    .principal("pm").and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)

            ;
        }

    }

}
