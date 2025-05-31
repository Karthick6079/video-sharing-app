package com.karthick.videosharingapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationRefreshQueue {

    private static final String REDIS_REFRESH_SET = "recommendation:refresh:pending";

    private final StringRedisTemplate redisTemplate;

    public void markUserForRefresh(String userId) {
        redisTemplate.opsForSet().add(REDIS_REFRESH_SET, userId);
    }

    public Set<String> getAllMarkedUsers() {
        return redisTemplate.opsForSet().members(REDIS_REFRESH_SET);
    }

    public void clearUser(String userId) {
        redisTemplate.opsForSet().remove(REDIS_REFRESH_SET, userId);
    }


}
