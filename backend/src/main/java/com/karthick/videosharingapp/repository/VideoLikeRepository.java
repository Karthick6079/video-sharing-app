package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.domain.TopicsOnly;
import com.karthick.videosharingapp.entity.VideoLike;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoLikeRepository extends MongoRepository<VideoLike, String> {

     void deleteByUserIdAndVideoId(String userId, String videoId);

     Optional<VideoLike> findByUserIdAndVideoId(String userId, String videoId);

     @Aggregation(pipeline = {
             "{$match: { userId: ?0 }}",
             "{$lookup: {from: 'videos',let: {videoId: '$videoId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$videoId'}]}}}],as: 'video_info'}}",
             "{$unwind: '$video_info'}",
             "{$lookup: {from: 'users',let: {userId: '$video_info.userId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$userId'}]}}}],as: 'user_info'}}",
             "{$sort: {likedAt: -1}}",
             "{$skip: ?1}",
             "{$limit: ?2}",
             "{$project: {_id: 1,userId: 1,videoId: 1,likedAt: 1,description: '$video_info.description',title: '$video_info.title',likes: '$video_info.likes',dislikes: '$video_info.dislikes',views: '$video_info.views',username: '$user_info.name',userDisplayName: '$user_info.displayName',userPicture: '$user_info.picture', tags: '$video_info.tags',status: '$video_info.status',videoUrl: '$video_info.videoUrl',thumbnailUrl: '$video_info.thumbnailUrl', publishedAt: '$video_info.publishedAt'}}"
     })
     List<VideoLike> getLikedVideos(String userId, int skip, int page);

     @Aggregation( pipeline = {
             "{ $match: { userId: ?0, likedAt: { $gte: ?1 } } }",
             "{ $unwind: '$watchTopics' }",
             "{ $group: { _id: null, topics: { $addToSet: '$watchTopics' } } }",
             "{ $project: { _id: 0, topics: 1 } }"
     })
     List<TopicsOnly> findWatchTopicsByUserIdAndLikedAt(String userId, Instant fromDate);

}
