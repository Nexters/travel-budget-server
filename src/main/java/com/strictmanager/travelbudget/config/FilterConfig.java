package com.strictmanager.travelbudget.config;

import com.strictmanager.travelbudget.infra.auth.JwtTokenUtil;
import com.strictmanager.travelbudget.web.filter.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class FilterConfig {

    @Bean
    public JwtRequestFilter jwtRequestFilter(
        UserDetailsService userService, JwtTokenUtil jwtAccessTokenUtil
    ) {
        return new JwtRequestFilter(userService, jwtAccessTokenUtil);
    }
}
