package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.domain.dto.ReactionCountResponse;
import com.karthick.videosharingapp.domain.dto.VideoUserInfoDTO;
import com.karthick.videosharingapp.entity.Video;
import com.karthick.videosharingapp.interfaces.VideoRepositoryCustom;
import com.karthick.videosharingapp.interfaces.VideoWatchRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.karthick.videosharingapp.constants.DatabaseConstants.*;

@Repository
@RequiredArgsConstructor
public class VideoRepositoryCustomImpl implements VideoRepositoryCustom {


    private final Logger logger = LoggerFactory.getLogger(VideoRepositoryCustomImpl.class);

    private final MongoTemplate mongoTemplate;

    private final VideoRepository videoRepository;

    @Override
    public List<Video> findTrendingVideos(int limit) {
        logger.info("Fetching the trending videos for guest user from DB");
        long now = System.currentTimeMillis();
        long THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000; // 30 days in ms

        logger.debug("Fetching trending videos from database process initiated");
        List<Video> trendingVideos = videoRepository.findTrendingVideos(THIRTY_DAYS_MS, now, limit);
        logger.debug("Fetching trending videos from database process completed");

        return trendingVideos;
    }

    @Transactional
    public void updatedUserVideoReactions(String userId, String videoId, VideoUserInfoDTO videoUserInfoDTO){

        Query query = new Query(Criteria.where(USER_ID).is(userId).and(VIDEO_ID).is(videoId));

        boolean isAlreadyLiked = mongoTemplate.exists(query, LIKED_VIDEOS_COLLECTION);
        boolean isAlreadyDisliked = mongoTemplate.exists(query, DISLIKED_VIDEOS_COLLECTION);

        videoUserInfoDTO.setUserLiked(isAlreadyLiked);
        videoUserInfoDTO.setUserDisliked(isAlreadyDisliked);

    }


}