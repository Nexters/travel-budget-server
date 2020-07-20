package com.strictmanager.travelbudget.config;

import com.strictmanager.travelbudget.domain.user.UserService;
import com.strictmanager.travelbudget.infra.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserService(userRepository);
    }
}
