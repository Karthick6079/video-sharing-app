package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.domain.dto.ReactionCountResponse;
import com.karthick.videosharingapp.entity.VideoLike;
import com.karthick.videosharingapp.interfaces.VideoDislikeRepositoryCustom;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static com.karthick.videosharingapp.constants.DatabaseConstants.*;

@Repository
public class VideoDislikeRepositoryCustomImpl implements VideoDislikeRepositoryCustom {

    private final Logger logger = LoggerFactory.getLogger(VideoDislikeRepositoryCustomImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @param userId
     * @param days
     * @return
     */
    @Override
    public List<String> findRecentDislikedTopicsByUsers(String userId, int days) {

        logger.info("Finding recently disliked topics by user process initiated");

        logger.info("Getting latest disliked videos of current user");
        // Step 1: Get the latest likedAt
        Criteria criteria = Criteria.where(USER_ID).is(userId);
        Query query = new Query(criteria).with(Sort.by(Sort.Direction.DESC, LIKED_AT_COLUMN)).limit(1);

        VideoLike latestDisliked = mongoTemplate.findOne(query, VideoLike.class, DISLIKED_VIDEOS_COLLECTION);

        if(latestDisliked == null) return Collections.emptyList();

        Instant toDate = latestDisliked.getLikedAt();
        Instant fromDate = toDate.minus(Duration.ofDays(days));
        logger.info("Get user disliked topics for {}-day window", days);
        // Step 2: Aggregation to get topics from 3-day window

        AggregationOperation match = Aggregation.match(Criteria.where(USER_ID).is(userId).and(DISLIKE_AT_COLUMN).gte(fromDate).lte(toDate));
        AggregationOperation unwind = Aggregation.unwind(DISLIKE_TOPICS_COLUMN);
        AggregationOperation group = Aggregation.group().addToSet(DISLIKE_TOPICS_COLUMN).as(TOPICS_ALIAS);
        AggregationOperation project = Aggregation.project(TOPICS_ALIAS).andExclude(ID);

        Aggregation aggregation =  Aggregation.newAggregation(match, unwind, group, project);

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, DISLIKED_VIDEOS_COLLECTION, Document.class);
        Document result = results.getUniqueMappedResult();
        List<String> resultFinal = result != null ? (List<String>) result.get(TOPICS_ALIAS) : Collections.emptyList();

        logger.info("Finding recent disliked topics by user process completed");

        return resultFinal;
    }


    @Transactional
    public ReactionCountResponse toggleDislike(String userId, String videoId, List<String> dislikeTopics) {
        Query likeQuery = new Query(Criteria.where(USER_ID).is(userId).and(VIDEO_ID).is(videoId));
        Query videoQuery = new Query(Criteria.where(ID).is(videoId));

        boolean alreadyLiked = mongoTemplate.exists(likeQuery, LIKED_VIDEOS_COLLECTION);
        boolean alreadyDisliked = mongoTemplate.exists(likeQuery, DISLIKED_VIDEOS_COLLECTION);

        if (alreadyDisliked) {
            // 1. Remove dislike
            mongoTemplate.remove(likeQuery, DISLIKED_VIDEOS_COLLECTION);

            // 2. Decrement dislike count
            Update update = new Update().inc(DISLIKES_COLUMN, -1);
            mongoTemplate.updateFirst(videoQuery, update, VIDEOS_COLLECTION);

        } else {
            // 1. Add to dislikedVideos
            Document liked = new Document(USER_ID, userId)
                    .append(VIDEO_ID, videoId)
                    .append(DISLIKE_AT_COLUMN, Instant.now())
                    .append(DISLIKE_TOPICS_COLUMN,dislikeTopics);
            mongoTemplate.insert(liked, DISLIKED_VIDEOS_COLLECTION);

            // 2. Increment likeCount
            Update update = new Update().inc(DISLIKES_COLUMN, 1);


            // 3. If previously disliked, remove and decrement dislikeCount
            if (alreadyLiked) {
                mongoTemplate.remove(likeQuery, LIKED_VIDEOS_COLLECTION);
                update.inc(LIKES_COLUMN, -1);
            }
            mongoTemplate.updateFirst(videoQuery, update, VIDEOS_COLLECTION);
        }

        return getLikeDislikeCount(videoId);
    }


    private ReactionCountResponse getLikeDislikeCount(String videoId) {
        Query query = new Query(Criteria.where("_id").is(videoId));
        Document video = mongoTemplate.findOne(query, Document.class, VIDEOS_COLLECTION);

        long likeCount = video != null ? video.getLong(LIKES_COLUMN) : 0L;
        long dislikeCount = video != null ? video.getLong(DISLIKES_COLUMN) : 0L;

        ReactionCountResponse response = new ReactionCountResponse(likeCount, dislikeCount);

        logger.info("Fetched latest like and dislike count and returning");

        return response;
    }
}
