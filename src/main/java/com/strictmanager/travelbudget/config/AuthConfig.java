package com.strictmanager.travelbudget.config;

import com.strictmanager.travelbudget.infra.auth.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Bean
    public JwtTokenUtil jwtAccessTokenUtil(
        @Value("${auth.jwt.access.secret}") String secret,
        @Value("${auth.jwt.access.expire-hours}") Long expireHours
    ) {
        return new JwtTokenUtil(secret, expireHours);
    }

    @Bean
    public JwtTokenUtil jwtRefreshTokenUtil(
        @Value("${auth.jwt.refresh.secret}") String secret,
        @Value("${auth.jwt.refresh.expire-hours}") Long expireHours
    ) {
        return new JwtTokenUtil(secret, expireHours);
    }
}
