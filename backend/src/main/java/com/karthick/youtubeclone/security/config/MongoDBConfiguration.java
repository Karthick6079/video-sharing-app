package com.karthick.youtubeclone.security.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.validation.Valid;
import org.bson.json.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

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
