package com.strictmanager.travelbudget.config;

import com.strictmanager.travelbudget.infra.auth.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {
    @Bean
    public JwtTokenUtil jwtTokenUtil(@Value("${auth.jwt.secret}") String secret) {
        return new JwtTokenUtil(secret);
    }
}
