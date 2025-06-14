package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.domain.TopicsOnly;
import com.karthick.videosharingapp.entity.VideoWatch;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReadPreference;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface VideoWatchRepository extends MongoRepository<VideoWatch, String> {

    @Aggregation(pipeline = {
            "{$match: { userId: ?0 }}",
            "{$lookup: {from: 'videos',let: {videoId: '$videoId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$videoId'}]}}}],as: 'video_info'}}",
            "{$unwind: '$video_info'}",
            "{$lookup: {from: 'users',let: {userId: '$video_info.userId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$userId'}]}}}],as: 'user_info'}}",
            "{$sort: {watchedAt: -1}}",
            "{$skip: ?1}",
            "{$limit: ?2}",
            "{$project: {_id: 1,userId: 1,videoId: 1,watchedAt: 1,description: '$video_info.description',title: '$video_info.title',likes: '$video_info.likes',dislikes: '$video_info.dislikes',views: '$video_info.views',username: '$user_info.name',userDisplayName: '$user_info.displayName',userPicture: '$user_info.picture', tags: '$video_info.tags',status: '$video_info.status',videoUrl: '$video_info.videoUrl',thumbnailUrl: '$video_info.thumbnailUrl', publishedAt: '$video_info.publishedAt'}}"
    })
    @ReadPreference("secondary")
    List<VideoWatch> getUserVideoWatchHistory(String userId, int skip, int limit);




    @Aggregation( pipeline = {
            "{ $match: { userId: ?0, watchedAt: { $gte: ?1 } } }",
            "{ $unwind: '$watchTopics' }",
            "{ $group: { _id: null, topics: { $addToSet: '$watchTopics' } } }",
            "{ $project: { _id: 0, topics: 1 } }"
    })
    List<TopicsOnly> findWatchTopicsByUserIdAndWatchedAt(String userId, Instant fromDate);

    @Aggregation(pipeline = {
            "{$match: {userId:?0}}",
            "{ $sort: {watchedAt: -1 }}",
            "{$limit: ?1}",
            "{$project:{_id:0,videoId:1}}"
    })
    List<String> findTopWatchedVideoIdsByUser(String userId, Integer size);

    @Aggregation(pipeline = {
            "{$match: {videoId: {$in: ?0}, userId: {$ne: ?1}}}",
            "{$project:{_id:0,userId:1}}"
    })
    List<String> findSimilarUsersIds(List<String> userWatchedVideoIds, String userId);

    @Aggregation(pipeline = {
            "{$match: {userId: {$in: ?0}, videoId: {$nin: ?1}}}",
            "{$group:{_id:'$userId', recentlyWatched: {$topN: { output:['$videoId'], sortBy:{'watchedAt':-1},n:?2 }}}}",
            "{$unwind: '$recentlyWatched' }",
            "{$project:{_id:0,videoId:'$recentlyWatched'}}"
    })
    List<String> findVideoWatchedByUsers(List<String> similarUserIds, List<String> userWatchedVideoIds, Integer maxVideosPerUser);

    @Aggregation(pipeline = {
            "{ $match:{watchedAt:{$gte:?0}}}",
            "{ $unwind: '$watchTopics' }",
            "{ $group: { _id: '$watchTopics', count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }",
            "{ $limit: ?1}",
            "{ $project: { _id: 0, topic: '$_id' } }"
    })
    List<String> getMostPopularWatchedTopics(Instant from, int limit);
}
