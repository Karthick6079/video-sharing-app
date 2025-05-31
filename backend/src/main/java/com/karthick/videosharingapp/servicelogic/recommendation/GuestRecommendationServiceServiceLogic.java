package com.karthick.videosharingapp.servicelogic.recommendation;

import com.karthick.videosharingapp.domain.ScoredVideo;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GuestRecommendationServiceServiceLogic implements RecommendationService {

    private final Logger logger = LoggerFactory.getLogger(GuestRecommendationServiceServiceLogic.class);

    private final VideoRepository videoRepository;

    private final VideoWatchRepository videoWatchRepository;

    private final VideoRepositoryCustomImpl videoRepositoryCustom;

    private final StringRedisTemplate redisTemplate;

    private final MapperUtil mapperUtil;

    private static final String CACHE_KEY_PREFIX = "recommendation:guest";


    /**
     * @param userId
     * @param pageable
     * @return
     */
    @Override
    public List<VideoUserInfoDTO> getRecommendationVideos(String userId, Pageable pageable) {
        return List.of();
    }

    public List<VideoUserInfoDTO> getRecommendationVideos(Pageable pageable){

        logger.info("Guest User Video recommendation process initiated");
        String cacheKey = CACHE_KEY_PREFIX;
        Long total = redisTemplate.opsForList().size(cacheKey);

        if (total == null || total == 0) {
            List<String> rankedVideoIds = computeAndCacheGuestRecommendations();
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
                .map( video -> mapperUtil.map(video, VideoUserInfoDTO.class) )
                .collect(Collectors.toMap(VideoUserInfoDTO::getId, v -> v));

        List<VideoUserInfoDTO> orderedVideos = pageIds.stream()
                .map(videoMap::get)
                .filter(Objects::nonNull)
                .toList();
        logger.info("Guest User Video recommendation process completed");
        return orderedVideos;
    }

    @Async
    private List<String> computeAndCacheGuestRecommendations() {
        // 1. Find trending videos

        List<Video> trendingVideos = videoRepositoryCustom.findTrendingVideos(20);

        // Find Most Popular topics

        logger.info("Finding most popular topics and finding the respective videos");
        List<String> videoTopics = videoWatchRepository.getMostPopularWatchedTopics(10);

        // 2. Find Popular videos
        List<Video> popularTopicVideos = videoRepository.findByTagsIn(videoTopics);

        List<Video> recentVideos = videoRepository.findByPublishedAtAfter(
                Instant.now().minus(30, ChronoUnit.DAYS)
        );

        // 3. Merge and deduplicate videos
        logger.info("Merging all videos and deduplicating");
        Map<String, Video> mergeMap = new HashMap<>();

        Stream.of(trendingVideos, popularTopicVideos, recentVideos)
                .flatMap(Collection::stream)
                .forEach( video -> mergeMap.putIfAbsent(video.getId(), video));

        //4. Compute the score the for the videos
        logger.info("Computing the score for fetched videos and ranking based computescore");
        List<ScoredVideo> scoredVideos = mergeMap.values().stream().map(
                        video -> {
                            double score = computeGuestScore(video);
                            return new ScoredVideo(video, score);
                        }
                ).sorted(Comparator.comparingDouble(ScoredVideo::getScore).reversed())
                .limit(50)
                .toList();


        List<String> rankedIds = scoredVideos.stream().map(v -> v.getVideo().getId()).toList();
        logger.info("Storing the ranked ids in cache with TTL 15 mins");
        String CACHE_KEY = CACHE_KEY_PREFIX;
        redisTemplate.delete(CACHE_KEY);
        redisTemplate.opsForList().rightPushAll(CACHE_KEY, rankedIds);
        redisTemplate.expire(CACHE_KEY, Duration.ofMinutes(15));

        return rankedIds;
    }

    private double computeGuestScore(Video video) {
        double score = 0;
        score += (video.getLikes() .doubleValue()* 0.002) + (video.getViewCount().doubleValue() * 0.0002);
        long daysOld = ChronoUnit.DAYS.between(video.getPublishedAt(), Instant.now());
        score += Math.max(0, 30 - daysOld) * 0.1;
        return score;
    }

}
