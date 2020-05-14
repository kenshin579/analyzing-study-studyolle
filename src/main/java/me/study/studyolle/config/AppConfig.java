package me.study.studyolle.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.spi.NameTokenizer;
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

    @Bean
    public ModelMapper modelMapper() {
        // Model Mapper는 기본적으로 Nested 한 프로퍼티를 바인딩 할 수 있다.
        // 초기 설정으로 Camel Case로 되어 있는 부분은 UnberScore로 변경하여 Camel Case로 선언한 변수명을 바인딩 하기 위해 선언.
        // Nested한 변수를 바인딩하는건 잘 되려나 ?
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        return modelMapper;
    }
}
