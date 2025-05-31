package com.karthick.videosharingapp.service;

import com.karthick.videosharingapp.servicelogic.recommendation.GuestRecommendationServiceServiceLogic;
import com.karthick.videosharingapp.servicelogic.recommendation.UserRecommendationServiceServiceLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RecommendationCacheRefresher {

    private final Logger logger = LoggerFactory.getLogger(RecommendationCacheRefresher.class);

    private static final String CACHE_KEY_PREFIX = "recommendations::";

    @Autowired
    private RecommendationRefreshQueue refreshQueue;

    @Autowired
    private UserRecommendationServiceServiceLogic recommendationService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedRate = 180000) // Every 3 mins
    public void refreshUserRecommendations() {
        Set<String> userIds = refreshQueue.getAllMarkedUsers();
        if (userIds == null || userIds.isEmpty()) return;

        for (String userId : userIds) {
            try {
                logger.info("Recommendation cache refreshing for userId: {}", userId);
//                Pageable pageable = PageRequest.of(0, 100); // Cache top 100 recs
                recommendationService.computeAndCacheRecommendations(userId);
                refreshQueue.clearUser(userId); // Done
            } catch (Exception e) {
                // Optionally log failure, and skip this user
               logger.error("Exception occurred while refresh the recommendations", e);
            }
        }
    }
}
