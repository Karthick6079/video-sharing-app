package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.entity.VideoLike;
import com.karthick.videosharingapp.entity.VideoWatch;
import com.karthick.videosharingapp.interfaces.VideoLikeRepositoryCustom;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Repository
public class VideoLikeRepositoryCustomImpl implements VideoLikeRepositoryCustom {

    private final Logger logger = LoggerFactory.getLogger(VideoLikeRepositoryCustomImpl.class);

    private static final String COLLECTION_NAME = "likedVideos";

    @Autowired
    private MongoTemplate mongoTemplate;
    
    /**
     * @param userId 
     * @param days
     * @return
     */
    @Override
    public List<String> findRecentLikedTopicsByUsers(String userId, int days) {

        logger.info("Finding recently liked topics by user process initiated");

        logger.info("Getting latest liked videos of current user");
        // Step 1: Get the latest likedAt
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = new Query(criteria).with(Sort.by(Sort.Direction.DESC, "likedAt")).limit(1);

        VideoLike latestLiked = mongoTemplate.findOne(query, VideoLike.class, COLLECTION_NAME);

        if(latestLiked == null) return Collections.emptyList();

        Instant toDate = latestLiked.getLikedAt();
        Instant fromDate = toDate.minus(Duration.ofDays(days));
        logger.info("Get user liked topics for {}-day window", days);
        // Step 2: Aggregation to get topics from 3-day window

        AggregationOperation match = Aggregation.match(Criteria.where("userId").is(userId).and("likedAt").gte(fromDate).lte(toDate));
        AggregationOperation unwind = Aggregation.unwind("likeTopics");
        AggregationOperation group = Aggregation.group().addToSet("likeTopics").as("topics");
        AggregationOperation project = Aggregation.project("topics").andExclude("_id");

        Aggregation aggregation =  Aggregation.newAggregation(match, unwind, group, project);

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, COLLECTION_NAME, Document.class);
        Document result = results.getUniqueMappedResult();
        List<String> resultFinal = result != null ? (List<String>) result.get("topics") : Collections.emptyList();

        logger.info("Finding recent liked topics by user process completed");

        return resultFinal;
    }
}
