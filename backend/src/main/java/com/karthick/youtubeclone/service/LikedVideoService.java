package com.karthick.youtubeclone.service;

import com.karthick.youtubeclone.entity.LikedVideo;
import com.karthick.youtubeclone.entity.VideoUserInfo;
import com.karthick.youtubeclone.repository.LikedVideoRepo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.lte;

@Service
@RequiredArgsConstructor
public class LikedVideoService {


    private final LikedVideoRepo likedVideoRepo;



    /**
     * This method update watched video into table if the specific video is already watched in current day
     * the existing video document data updated, Otherwise new document will be inserted at collection
     *
     * @param videoUserInfo
     * @param database
     */
    public void addLikedVideoInDB(String userId, String videoId, Set<String> topics) {


        LikedVideo likedVideo = new LikedVideo();
        likedVideo.setVideoId(videoId);
        likedVideo.setUserId(userId);
        likedVideo.setLikedOn(LocalDateTime.now());
        likedVideo.setLikedTopics(topics);

        likedVideoRepo.save(likedVideo);


    }


    public void removeLikedVideoFromDB(String userId, String videoId){

        likedVideoRepo.deleteByUserIdAndVideoId(userId, videoId);

    }


}
