package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.entity.Video;
import com.karthick.videosharingapp.entity.VideoUserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReadPreference;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;


@Repository
public interface VideoRepository extends MongoRepository<Video, String> {



    @Aggregation(pipeline = {
            "{$match: { _id: ?0 , status:'PUBLIC'}}",
            "{$lookup: {from: 'users',let: {userId: '$userId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$userId'}]}}}],as: 'user_info'}}",
            "{$unwind: '$user_info'}",
            "{$project: {_id: 1,videoId: '$_id', title: 1,description: 1,userId: 1,likes: 1,dislikes: 1,tags: 1,status: 1,videoUrl: 1,thumbnailUrl: 1,views: 1," +
                    "publishedAt: 1,username: '$user_info.name',userDisplayName: '$user_info.nickname',userPicture: '$user_info.picture'}}"
    })
    @ReadPreference("secondary")
    VideoUserInfo getVideoUserInfo(String videoId);

    @Aggregation(pipeline = {
            "{$match: { _id: {$in:?0}, status:'PUBLIC'}}",
            "{$lookup: {from: 'users',let: {userId: '$userId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$userId'}]}}}],as: 'user_info'}}",
            "{$unwind: '$user_info'}",
            "{$project: {_id: 1,videoId: '$_id', title: 1,description: 1,userId: 1,likes: 1,dislikes: 1,tags: 1,status: 1,videoUrl: 1,thumbnailUrl: 1,views: 1," +
                    "publishedAt: 1,username: '$user_info.name',userDisplayName: '$user_info.nickname',userPicture: '$user_info.picture'}}"
    })
    @ReadPreference("secondary")
    List<VideoUserInfo> getVideoUserInfoByVideoIds(List<String> videoIds);

    @Query( value="{}", fields = "{_id:1}")
    Page<Video> findAllIds(Pageable pageable);


    Page<Video> findAllById(List<String> videoIds, Pageable pageable);

    List<Video> findByUserIdIn(List<String> userIds);

    @Aggregation(pipeline = {
            "{$match: { userId: { $in:?0}}}",
            "{$sort: { publishedAt: -1 }}",
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
            "{$match: {title:{ $regex:?0, $options:'i'}, status:'PUBLIC'}}",
            "{$lookup: {from: 'users',let: {userId: '$userId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$userId'}]}}}],as: 'user_info'}}",
            "{$unwind: '$user_info'}",
            "{$project: {_id: 1,videoId: '$_id', title: 1,description: 1,userId: 1,likes: 1,dislikes: 1,tags: 1,status: 1,videoUrl: 1,thumbnailUrl: 1,views: 1," +
                    "publishedAt: 1,username: '$user_info.name',userDisplayName: '$user_info.nickname',userPicture: '$user_info.picture'}}"
    })
    List<VideoUserInfo> findVideosBySearchText(String searchText);
    @Aggregation(pipeline = {
            "{$unwind:'$tags'}",
            "{$sort:{views:-1,likes:-1}}",
            "{$group:{_id:{'tags':'$tags','views':'$views','likes':'$likes'}}}",
            "{$sort:{'_id.views':-1,'_id.likes':-1}}",
            "{$project:{_id:0,topics:'$_id.tags'}}"
    })
    List<String> getTrendingTopics();


    @Aggregation(pipeline = {
            "{$match: {tags: {$eq:?0} }}",
            "{$lookup: {from: 'users',let: {userId: '$userId'},pipeline: [{$match: {$expr: {$eq: ['$_id',{$toObjectId: '$$userId'}]}}}],as: 'user_info'}}",
            "{$unwind: '$user_info'}",
            "{$project: {_id: 1,videoId: '$_id', title: 1,description: 1,userId: 1,likes: 1,dislikes: 1,tags: 1,status: 1,videoUrl: 1,thumbnailUrl: 1,views: 1," +
                    "publishedAt: 1,username: '$user_info.name',userDisplayName: '$user_info.nickname',userPicture: '$user_info.picture'}}"
    })
    List<VideoUserInfo> findVideosByTopic(String topic);



    List<Video> findByTagsIn(List<String> topics);

    List<Video> findByIdIn(List<String> videoIds);

    @Query("{status:'PUBLIC','publishedAt': { $gte: ?0 } }")
    List<Video> findByPublishedAtAfter(Instant date);


    @Aggregation(pipeline = {
            "{$match: { status: 'PUBLIC' }}",
            "{$addFields: {trendingScore: { $add: [{ $multiply: ['$views', 0.5] },{ $multiply: ['$likes', 0.3] },{" +
                    "$multiply: [{$subtract: [?0,{ $subtract: [?1, { $toLong: '$publishedAt' }] }]},0.0000002]}]}}}",
            "{$sort: { trendingScore: -1 } }",
            "{$limit:?2}",
            "{$project:{_id:1, likes:1, views:1, tags:1, title:1, publishedAt:1}}"
    })
    List<Video> findTrendingVideos(Long THIRTY_DAYS_ML, Long now, int limit);






}
