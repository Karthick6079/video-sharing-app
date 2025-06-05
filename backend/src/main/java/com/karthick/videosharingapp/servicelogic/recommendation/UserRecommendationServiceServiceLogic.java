package com.karthick.videosharingapp.servicelogic.recommendation;

import com.karthick.videosharingapp.domain.ScoredVideo;
import com.karthick.videosharingapp.domain.dto.ChannelIdDTO;
import com.karthick.videosharingapp.domain.dto.VideoUserInfoDTO;
import com.karthick.videosharingapp.entity.Video;
import com.karthick.videosharingapp.entity.VideoUserInfo;
import com.karthick.videosharingapp.interfaces.RecommendationService;
import com.karthick.videosharingapp.repository.*;
import com.karthick.videosharingapp.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserRecommendationServiceServiceLogic implements RecommendationService {

    private final Logger logger = LoggerFactory.getLogger(UserRecommendationServiceServiceLogic.class);
    
    private final VideoRepository videoRepository;
    
    private final VideoWatchRepository videoWatchRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final VideoRepositoryCustomImpl videoRepositoryCustom;

    private final VideoWatchRepositoryCustomImpl videoWatchRepositoryCustom;

    private final VideoLikeRepositoryCustomImpl videoLikeRepositoryCustom;

    private final StringRedisTemplate redisTemplate;

    private final MapperUtil mapperUtil;

    private static final String CACHE_KEY_PREFIX = "recommendation:user:";


    public List<VideoUserInfoDTO> getRecommendationVideos(String userId, Pageable pageable) {

        logger.info("Logged User Video recommendation process initiated");
        String cacheKey = CACHE_KEY_PREFIX + userId;
        Long total = redisTemplate.opsForList().size(cacheKey);

        if (total == null || total == 0) {
            List<String> rankedVideoIds = computeAndCacheRecommendations(userId);
            total = (long) rankedVideoIds.size();
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total.intValue());

        logger.info("Ranked videos id sliced from cache for page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        List<String> pageIds = redisTemplate.opsForList().range(cacheKey, start, end - 1);
        if (pageIds == null || pageIds.isEmpty()) {
            return Collections.emptyList();
        }

        logger.info("Fetching video and user information for ranked video ids");
        List<VideoUserInfo> videos = videoRepository.getVideoUserInfoByVideoIds(pageIds);
        Map<String, VideoUserInfoDTO> videoMap = videos.stream()
                .map(video -> mapperUtil.map(video, VideoUserInfoDTO.class))
                .collect(Collectors.toMap(VideoUserInfoDTO::getId, v -> v));

        List<VideoUserInfoDTO> orderedVideos = pageIds.stream()
                .map(videoMap::get)
                .filter(Objects::nonNull)
                .toList();
        logger.info("Logged User Video recommendation process completed");
        return orderedVideos;
    }

    @Async
    public List<String> computeAndCacheRecommendations(String userId) {
        // 1. Find user interested topics
        logger.info("Fetching user interested topic videos");
        List<String> watchedTopics = videoWatchRepositoryCustom.findRecentWatchTopicsByUsers(userId, 3);
        List<String> likedTopics = videoLikeRepositoryCustom.findRecentLikedTopicsByUsers(userId,3);

        // The channels user subscribed
        logger.info("Fetching the user subscribed channel Ids");
        List<ChannelIdDTO> subscribedChannelIdDTOs = subscriptionRepository.findChannelIdBySubscriberId(userId);

        List<String> subscribedChannelIds = subscribedChannelIdDTOs.stream().map(ChannelIdDTO::getChannelId).toList();

        List<String> uniqueInterestList = Stream.of(watchedTopics, likedTopics)
                .flatMap(List::stream)
                .distinct()
                .toList();


        // 2. Find videos based on user interest
        List<Video> contentBasedVideos = videoRepository.findByTagsIn(uniqueInterestList);
        List<Video> subscribedUsersVideos = videoRepository.findByUserIdIn(subscribedChannelIds);

        logger.info("Fetching videos of user who has similar interest to current user");
        List<Video> collaborateVideos = getCollaborateVideos(userId);

        Map<String, Video> mergeMap = new HashMap<>();

        logger.info("Merging all videos and deduplicate videos");
        // 3. Remove duplicate videos
        Stream.of(contentBasedVideos, subscribedUsersVideos, collaborateVideos)
                .flatMap(Collection::stream)
                .forEach( video -> mergeMap.putIfAbsent(video.getId(), video));

        int videosSize = mergeMap.size();

        // If user specific videos  size below 20 means, find some trending videos
        if(videosSize <= 20){
            List<Video> trendingVideos = videoRepositoryCustom.findTrendingVideos(25);
            Stream.of(trendingVideos)
                    .flatMap(Collection::stream)
                    .forEach( video -> mergeMap.putIfAbsent(video.getId(), video));
        }

        logger.info("Ranking the videos based computed score");
        // 4. Calculate the score for the videos based user interest and factors
        List<ScoredVideo> scoredVideos =  mergeMap.values().stream()
                 .map( video -> {
                     boolean isFromSubscribed = subscribedChannelIds.contains(userId);
                     double score = computeScore(video, uniqueInterestList, isFromSubscribed);
                     return new ScoredVideo(video, score);
                 })
                 .sorted(Comparator.comparingDouble(ScoredVideo::getScore).reversed())
                 .toList();

        logger.info("Store the ranked video ids in cache with 10mins TTL");

        // Cache first 50 videos
        List<String> rankedIds = scoredVideos.stream().map(v -> v.getVideo().getId()).limit(50).toList();
        String CACHE_KEY = CACHE_KEY_PREFIX + userId;
        redisTemplate.delete(CACHE_KEY);
        redisTemplate.opsForList().rightPushAll(CACHE_KEY, rankedIds);
        redisTemplate.expire(CACHE_KEY, Duration.ofMinutes(10));

        return rankedIds;
    }

    private double computeScore(Video video, List<String> userTags, boolean fromSubscribed) {
        double score = 0;
        long matchCount = video.getTags().stream().filter(userTags::contains).count();
        score += matchCount * 0.5;
        score += (video.getLikes().doubleValue() * 0.002) + (video.getViews().doubleValue() * 0.0001);
        long daysOld = ChronoUnit.DAYS.between(video.getPublishedAt(), Instant.now());
        score += Math.max(0, 30 - daysOld) * 0.1;
        if (fromSubscribed) score += 0.3;
        return score;
    }


    private List<Video> getCollaborateVideos(String userId){

        int limit = 20;

        List<String> userWatchedVideoIds = videoWatchRepository.findTopWatchedVideoIdsByUser(userId, 20);

        List<String> similarUserIds = videoWatchRepository.findSimilarUsersIds(userWatchedVideoIds, userId);

        List<String> candidateVideoIds = videoWatchRepository.findVideoWatchedByUsers(similarUserIds, userWatchedVideoIds, 10);

        if(candidateVideoIds.isEmpty()) // If similar no videos found return empty list
            return Collections.emptyList();

        // Step 4: Count and sort by popularity among those users
        Map<String, Long> frequency = candidateVideoIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<String> finalVideoIds =  frequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();

        return videoRepository.findByIdIn(finalVideoIds);

    }

    
    
}
