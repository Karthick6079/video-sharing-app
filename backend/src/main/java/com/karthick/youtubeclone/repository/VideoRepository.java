package com.karthick.youtubeclone.repository;

import com.karthick.youtubeclone.dto.LatestVideoDTO;
import com.karthick.youtubeclone.entity.Video;
import com.karthick.youtubeclone.entity.VideoUserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReadPreference;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VideoRepository extends MongoRepository<Video, String> {



    @Aggregation(pipeline = {
            "{$match: { _id: ?0 }}",
            "{$lookup: {from: 'users',let: {userId: '$userId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$userId'}]}}}],as: 'user_info'}}",
            "{$unwind: '$user_info'}",
            "{$project: {_id: 1,videoId: '$_id', title: 1,description: 1,userId: 1,likes: 1,disLikes: 1,tags: 1,videoStatus: 1,videoUrl: 1,thumbnailUrl: 1,viewCount: 1," +
                    "publishedDateAndTime: 1,username: '$user_info.name',userDisplayName: '$user_info.nickname',userPicture: '$user_info.picture'}}"
    })
    @ReadPreference("secondary")
    VideoUserInfo getVideoUserInfo(String videoId);

    @Query( value="{}", fields = "{_id:1}")
    Page<Video> findAllIds(Pageable pageable);


    Page<Video> findAllById(List<String> videoIds, Pageable pageable);
    Page<Video> findAllByUserId(List<String> userIds, Pageable pageable);
    @Aggregation(pipeline = {
            "{$match: { userId: { $in:?0}}}",
            "{$sort: { publishedDateAndTime: -1 }}",
            "{$group: {_id: '$userId',videoId: {$firstN:{input:'$_id', n : 2}}}}",
            "{$unwind:'$videoId'}",
            "{$project:{_id:0}}"
    })
    List<String> findLatestVideoFromUsers(List<String> userId, Pageable pageable);

    @Aggregation(pipeline = {
            "{$sample:{size:2}}",
            "{$project:{_id:1}}"
    })
    List<String> getShortsVideo();

    @Aggregation(pipeline = {
            "{$match: {title:{ $regex:?0, $options:'i'}}}",
            "{$lookup: {from: 'users',let: {userId: '$userId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$userId'}]}}}],as: 'user_info'}}",
            "{$unwind: '$user_info'}",
            "{$project: {_id: 1,videoId: '$_id', title: 1,description: 1,userId: 1,likes: 1,disLikes: 1,tags: 1,videoStatus: 1,videoUrl: 1,thumbnailUrl: 1,viewCount: 1," +
                    "publishedDateAndTime: 1,username: '$user_info.name',userDisplayName: '$user_info.nickname',userPicture: '$user_info.picture'}}"
    })
    List<VideoUserInfo> findVideosBySearchText(String searchText);



}
