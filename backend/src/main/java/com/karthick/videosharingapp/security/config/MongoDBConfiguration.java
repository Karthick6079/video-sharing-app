package com.karthick.videosharingapp.security.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoDBConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.uri}")
    private String uri;



    @Bean
    public MongoClient mongoClient(){
        return MongoClients.create(uri);
    }

    @Bean
    public MongoOperations mongoTemplate(MongoClient mongoClient){
        return new MongoTemplate(mongoClient, databaseName);
    }


}
