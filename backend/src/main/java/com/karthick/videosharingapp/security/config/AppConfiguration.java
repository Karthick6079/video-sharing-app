package com.karthick.videosharingapp.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.karthick.videosharingapp.domain.ZoneContext;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;


@Configuration
public class AppConfiguration {


    private final ZoneContext zoneContext;

    public AppConfiguration(ZoneContext zoneContext) {
        this.zoneContext = zoneContext;
    }

    @Bean
    public ModelMapper mapper(){
        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.addMappings(new PropertyMap<Video, VideoDTO>() {
//            @Override
//            protected void configure() {
//                skip(destination.getCreatedAt());
//            }
//        });

        modelMapper.getConfiguration().setFullTypeMatchingRequired(false);

        Converter<Instant, ZonedDateTime> instantZonedDateTimeConverter = context ->
                context.getSource() != null ? context.getSource().atZone(zoneContext.getZoneId()): null;

        Converter<Date, ZonedDateTime> DatetoZoneDateTimeConverter = context ->
                context.getSource() != null ? context.getSource().toInstant().atZone(zoneContext.getZoneId()) : null;

        modelMapper.addConverter(instantZonedDateTimeConverter);
        modelMapper.addConverter(DatetoZoneDateTimeConverter);

        return modelMapper;
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }




}
