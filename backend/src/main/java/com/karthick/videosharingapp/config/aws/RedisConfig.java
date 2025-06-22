package com.karthick.videosharingapp.config.aws;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String hostname;

    @Value("${spring.redis.port}")
    private int port;



    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(hostname);
        redisStandaloneConfiguration.setPort(port);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Integer> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Integer.class));
        return template;
    }

//    @PostConstruct
//    public void printRedisHost() {
//        int retries = 5;
//        int waitMs = 2000;
//
//        for (int i = 1; i <= retries; i++) {
//            try (var conn = redisConnectionFactory.getConnection()) {
//                String pong = conn.ping();
//                System.out.println("Redis connected: " + pong);
//                return;
//            } catch (Exception e) {
//                System.err.println("Attempt " + i + ": Redis not ready yet - " + e.getMessage());
//                try {
//                    Thread.sleep(waitMs);
//                } catch (InterruptedException ignored) {}
//            }
//        }
//
//        throw new RuntimeException("Redis not reachable after " + retries + " attempts");
//    }
}
