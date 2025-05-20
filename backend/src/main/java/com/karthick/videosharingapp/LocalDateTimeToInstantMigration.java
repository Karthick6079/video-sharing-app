package com.karthick.videosharingapp;

import java.util.Date;
import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Runs once, at startup, only when the "migrate-timestamps" profile is active.
 * After it succeeds, remove the profile or the whole bean.
 */
@Component
@Profile("migrate-timestamps")
public class LocalDateTimeToInstantMigration implements CommandLineRunner {

    private static final ZoneId LEGACY_ZONE = ZoneId.of("Asia/Kolkata");

    private final Logger logger = LoggerFactory.getLogger(LocalDateTimeToInstantMigration.class);

    private final MongoTemplate mongo;

    public LocalDateTimeToInstantMigration(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public void run(String... args) {

        logger.info("Migration process started video streaming app collections");

        Map<String, List<String>> input = new HashMap<>();
//        input.put("watchedVideos", List.of("watchedOn", "watchedAt"));
        input.put("videos", List.of("createdAt", "publishedAt"));
//        input.put("likeVideos", List.of("likedOn", "likedAt"));
//        input.put("likeComments", List.of("likedOn", "likedAt"));
//        input.put("dislikeComments", List.of("dislikedOn", "dislikedAt"));
//        input.put("comments", List.of("commentCreatedTime", "createdAt"));

        logger.info("Required hashmap prepared for migration");

        for(Map.Entry<String, List<String>> inputvalue :input.entrySet()){

            String collection = inputvalue.getKey();
            List<String> modifyColumnList = inputvalue.getValue();

            // 1️⃣ Fetch docs that still have the legacy field and lack the new one
            Query q = new Query()
                    .addCriteria(where(modifyColumnList.get(0)).exists(true));
//                    .addCriteria(where(modifyColumnList.get(1)).exists(false));



            logger.info("Migration started for collection: {}", collection);
//            logger.info("LocalDateField: {} to be migrated as ---> Instant: {}", modifyColumnList.get(0), modifyColumnList.get(1));

            try {
                List<Document> docs = mongo.find(q, Document.class, collection);
                if (docs.isEmpty()) {
                    logger.info("[Migration] Nothing to migrate, exiting. empty records: {}", collection);
                    continue;
                }


                BulkOperations bulk = mongo.bulkOps(BulkOperations.BulkMode.UNORDERED, collection);

                for (Document d : docs) {
                    Date instantAsDate = d.get(modifyColumnList.get(0), Date.class);
//                    Date date = d.get(modifyColumnList.get(0), Date.class);
//                    Date date = new Date(milliseconds);
                    Instant legacyInstant = instantAsDate.toInstant();
                    ZonedDateTime asLegacyZone = legacyInstant.atZone(ZoneId.of("Asia/Kolkata"));
                    Instant canonical = asLegacyZone.toInstant(); // this might look redundant, but preserves intent
                    // Convert -> UTC Instant
//                    Instant canonical = ldt.atZone(LEGACY_ZONE).toInstant();

//                    Document update = new Document("$set", new Document(modifyColumnList.get(1), canonical))
//                            // optionally remove the old field
//                            .append("$unset", new Document(modifyColumnList.get(0), ""));


                    Update update = new Update();
                    update.set(modifyColumnList.get(1), canonical);
//                    update.unset(modifyColumnList.get(0));

                    Query query = new Query(where("_id").is(d.get("_id")));

                    bulk.updateOne(query, update);
                }

                bulk.execute();   // ⚠️ RUNS the updates
                logger.info("Migration completed for collection: {}", collection);
            } catch (Exception e) {
                logger.error("Exception occurred during migration", e);
                throw new RuntimeException(e);
            }
        }


        logger.info("Migration process completed video streaming app collections");



    }
}
