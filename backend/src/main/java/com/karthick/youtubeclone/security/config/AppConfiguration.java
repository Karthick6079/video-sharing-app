package com.karthick.youtubeclone.security.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {


    @Bean
    public ModelMapper mapper(){
        return new ModelMapper();
    }
}
