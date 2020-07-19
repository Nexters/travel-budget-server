package com.strictmanager.travelbudget.config;

import com.strictmanager.travelbudget.domain.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
    @Bean
    public UserService userService() {
        return new UserService();
    }
}
