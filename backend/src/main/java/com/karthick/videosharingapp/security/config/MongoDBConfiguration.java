package com.karthick.videosharingapp.security.config;

import com.karthick.videosharingapp.domain.ZoneContext;
import com.karthick.videosharingapp.util.AppDateToZonedDateTimeConverter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;
import java.util.List;

@Configuration
public class MongoDBConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.uri}")
    private String uri;


    @Bean
    public MongoCustomConversions mongoCustomConversions( ZoneContext zoneContext){
        return new MongoCustomConversions(
                List.of(new AppDateToZonedDateTimeConverter(zoneContext))
        );
    }

    @Bean
    public MongoClient mongoClient(){
        return MongoClients.create(uri);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient){
        return new MongoTemplate(mongoClient, databaseName);
    }


}
