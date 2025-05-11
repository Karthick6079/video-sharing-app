package com.karthick.youtubeclone.config.aws;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

    @Value("${spring.cloud.aws.region}")
    private String region;

    @Value("${spring.cloud.aws.profile}")
    private String profile;


    @Bean
    @Profile("local")
    public S3Client createS3beanLocal(){
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(ProfileCredentialsProvider.create(profile))
                .build();
    }

    @Bean
    @Profile("cloud")
    public S3Client createS3beanCloud(){
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
    }
}
