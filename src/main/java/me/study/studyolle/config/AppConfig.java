package me.study.studyolle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { // spring security 에서 passwordencoder 가 하나만 등록 되어 있으면 자동으로 사용한다.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // bcrypt encoder
    }
}
