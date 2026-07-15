package com.study.tdd.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPasswordEncoderConfiguration {

    @Bean
    @Primary
    BCryptPasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
