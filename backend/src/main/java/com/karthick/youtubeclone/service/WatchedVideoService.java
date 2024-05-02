package com.karthick.youtubeclone.service;


import com.karthick.youtubeclone.entity.User;
import com.karthick.youtubeclone.entity.VideoUserInfo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.mongodb.client.model.Filters.*;

@Service
public class WatchedVideoService {


    /**
     * This method update watched video into table if the specific video is already watched in current day
     * the existing video document data updated, Otherwise new document will be inserted at collection
     *
     * @param videoUserInfo
     * @param database
     */
    public void updateWatchHistory(VideoUserInfo videoUserInfo, MongoDatabase database, User currentUser) {
        MongoCollection<Document> collection = database.getCollection("watchedVideos");

        // Query to filter

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime yesterday = LocalDateTime.now().with(LocalTime.MIN);

        Bson filter = and(
                eq("videoId", videoUserInfo.getId()),
                gt("watchedOn", yesterday),
                lte("watchedOn", now));


        //Update document
        Bson update  = Updates.combine(
                Updates.set("userId", currentUser.getId()),
                Updates.set("videoId", videoUserInfo.getId()),
                Updates.set("watchedOn", LocalDateTime.now()),
                Updates.set("tags", videoUserInfo.getTags())
        );
        //Setting upset value as true,
        UpdateOptions updateOptions = new UpdateOptions().upsert(true);

        UpdateResult updateResult = collection.updateOne(filter, update, updateOptions);

        if(updateResult.getModifiedCount() > 0){
            System.out.println("The watched video table updated!");
        }
    }
}
