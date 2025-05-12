package com.karthick.youtubeclone.config.aws;


import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@Profile("cloud")
public class AwsS3ConfigCloud {

    private final Logger logger = LoggerFactory.getLogger(AwsS3ConfigCloud.class);

    @Value("${spring.cloud.aws.region}")
    private String region;

    @Bean
    public S3Client createS3beanCloud(){
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public S3Presigner createS3Presigner(){
        return S3Presigner.builder().region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create()).build();
    }

    @PostConstruct
    public void postBeanConstruct(){
        logger.info("S3client bean created for Cloud Profile!");
        AwsCredentials creds = DefaultCredentialsProvider.create().resolveCredentials();
        logger.info("Using access key: {}", creds);
    }
}
