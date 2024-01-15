package com.karthick.youtubeclone.security.config;

import com.karthick.youtubeclone.dto.VideoDTO;
import com.karthick.youtubeclone.entity.Video;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {


    @Bean
    public ModelMapper mapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Video, VideoDTO>() {
            @Override
            protected void configure() {
                skip(destination.getPublishedDateAndTime());
            }
        });
        return modelMapper;
    }
}
