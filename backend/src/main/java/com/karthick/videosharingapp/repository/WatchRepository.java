package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.entity.Watch;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReadPreference;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchRepository extends MongoRepository<Watch, String> {

    @Aggregation(pipeline = {
            "{$match: { userId: ?0 }}",
            "{$lookup: {from: 'videos',let: {videoId: '$videoId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$videoId'}]}}}],as: 'video_info'}}",
            "{$unwind: '$video_info'}",
            "{$lookup: {from: 'users',let: {userId: '$video_info.userId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$userId'}]}}}],as: 'user_info'}}",
            "{$sort: {watchedAt: -1}}",
            "{$skip: ?1}",
            "{$limit: ?2}",
            "{$project: {_id: 1,userId: 1,videoId: 1,watchedAt: 1,description: '$video_info.description',title: '$video_info.title',likes: '$video_info.likes',disLikes: '$video_info.disLikes',viewCount: '$video_info.viewCount',username: '$user_info.name',userDisplayName: '$user_info.nickname',userPicture: '$user_info.picture', tags: '$video_info.tags',videoStatus: '$video_info.videoStatus',videoUrl: '$video_info.videoUrl',thumbnailUrl: '$video_info.thumbnailUrl', publishedAt: '$video_info.publishedAt'}}"
    })
    @ReadPreference("secondary")
    List<Watch> getUserVideoWatchHistory(String userId, int skip, int limit);


    List<String> findWatchTopicsByUserId(String userId);

}
