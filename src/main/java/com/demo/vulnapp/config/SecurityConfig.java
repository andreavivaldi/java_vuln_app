package com.demo.vulnapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Intentionally disabling all security protections for demo purposes
        http
            .csrf().disable()
            .headers().frameOptions().disable()
            .and()
            .authorizeRequests().anyRequest().permitAll();
    }
}
