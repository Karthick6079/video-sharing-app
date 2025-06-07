package com.karthick.videosharingapp.service;


import com.karthick.videosharingapp.entity.User;
import com.karthick.videosharingapp.domain.dto.VideoUserInfoDTO;
import com.karthick.videosharingapp.entity.VideoWatch;
import com.karthick.videosharingapp.repository.VideoWatchRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import static com.mongodb.client.model.Filters.*;

@Service
public class VideoWatchService {

    private final Logger logger = LoggerFactory.getLogger(VideoWatchService.class);

    @Autowired
    private VideoWatchRepository videoWatchRepository;


    /**
     * This method update watched video into table if the specific video is already watched in current day
     * the existing video document data updated, Otherwise new document will be inserted at collection
     *
     * @param videoUserInfo
     * @param database
     */
    public void updateWatchHistory(VideoUserInfoDTO videoUserInfo, MongoDatabase database, User currentUser) {

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        String watchDate = today.toString();


        MongoCollection<Document> collection = database.getCollection("watchedVideos");
        // Query to filter

        Instant now = Instant.now();

        Bson filter = and(
                eq("videoId", videoUserInfo.getId()),
                eq("userId", currentUser.getId()),
                eq("watchDate", watchDate));


        //Update document
        Bson update  = Updates.combine(
                Updates.set("userId", currentUser.getId()),
                Updates.set("videoId", videoUserInfo.getId()),
                Updates.set("watchedAt", now),
                Updates.set("watchDate", watchDate),
                Updates.set("watchTopics", videoUserInfo.getTags())
        );
        //Setting upset value as true,
        UpdateOptions updateOptions = new UpdateOptions().upsert(true);

        UpdateResult updateResult = collection.updateOne(filter, update, updateOptions);

        if(updateResult.getModifiedCount() > 0){
            logger.info("The user watch history updated");
        }
    }
}
