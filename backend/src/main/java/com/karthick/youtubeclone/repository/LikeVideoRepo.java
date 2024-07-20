package com.karthick.youtubeclone.repository;

import com.karthick.youtubeclone.entity.LikeVideo;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeVideoRepo extends MongoRepository<LikeVideo, String> {

     void deleteByUserIdAndVideoId(String userId, String videoId);

     Optional<LikeVideo> findByUserIdAndVideoId(String userId, String videoId);

     @Aggregation(pipeline = {
             "{$match: { userId: ?0 }}",
             "{$lookup: {from: 'videos',let: {videoId: '$videoId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$videoId'}]}}}],as: 'video_info'}}",
             "{$unwind: '$video_info'}",
             "{$lookup: {from: 'users',let: {userId: '$video_info.userId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$userId'}]}}}],as: 'user_info'}}",
             "{$sort: {likedOn: -1}}",
             "{$skip: ?1}",
             "{$limit: ?2}",
             "{$project: {_id: 1,userId: 1,videoId: 1,likedOn: 1,description: '$video_info.description',title: '$video_info.title',likes: '$video_info.likes',disLikes: '$video_info.disLikes',viewCount: '$video_info.viewCount',username: '$user_info.name',userDisplayName: '$user_info.nickname',userPicture: '$user_info.picture', tags: '$video_info.tags',videoStatus: '$video_info.videoStatus',videoUrl: '$video_info.videoUrl',thumbnailUrl: '$video_info.thumbnailUrl', publishedDateAndTime: '$video_info.publishedDateAndTime'}}"
     })
     List<LikeVideo> getLikedVideos(String userId, int skip, int page);

}
