package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.entity.Video;
import com.karthick.videosharingapp.interfaces.VideoRepositoryCustom;
import com.karthick.videosharingapp.interfaces.VideoWatchRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class VideoRepositoryCustomImpl implements VideoRepositoryCustom {


    private final Logger logger = LoggerFactory.getLogger(VideoRepositoryCustomImpl.class);

    private final MongoTemplate mongoTemplate;

    private final VideoRepository videoRepository;

    @Override
    public List<Video> findTrendingVideos(int limit) {
        logger.info("Fetching the trending videos for guest user from DB");
        long now = System.currentTimeMillis();
        long THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000; // 30 days in ms

        logger.debug("Fetching trending videos from database process initiated");
        List<Video> trendingVideos = videoRepository.findTrendingVideos(THIRTY_DAYS_MS, now, limit);
        logger.debug("Fetching trending videos from database process completed");

        return trendingVideos;
    }

//    @Override
//    public List<Video> findTrendingVideos(int limit) {
//        long now = System.currentTimeMillis(); // current time in ms
//
//        Aggregation aggregation = Aggregation.newAggregation(
//                // Step 1: Convert publishedAt to milliseconds and calculate age
//                Aggregation.project("title", "views", "likes", "publishedAt", "videoUrl", "tags")
//                        .and(ConvertOperators.ToLong.toLong("publishedAt")).as("publishedAtMillis")
//                        .andExpression("views").as("views")
//                        .andExpression("likes").as("likes"),
//
//                // Step 2: Calculate trending score = views * 0.5 + likes * 0.3 + freshness_score
//                Aggregation.addFields()
//                        .addField("trendingScore")
//                        .withValue(
//                                ArithmeticOperators.Add
//                                        .addValueOf(
//                                                ArithmeticOperators.Multiply.valueOf("views").multiplyBy(0.5)
//                                        )
//                                        .add(
//                                                ArithmeticOperators.Multiply.valueOf("likes").multiplyBy(0.3)
//                                        )
//                                        .add(
//                                                ArithmeticOperators.Multiply.valueOf(
//                                                        ArithmeticOperators.Subtract.valueOf(now)
//                                                                .subtract("publishedAtMillis")
//                                                ).multiplyBy(0.0000002) // freshness decay
//                                        )
//                        ).build(),
//
//                // Step 3: Sort by score and limit
//                Aggregation.sort(Sort.Direction.DESC, "trendingScore"),
//                Aggregation.limit(limit)
//        );
//
//        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "videos", Document.class);
//
//        return results.getMappedResults().stream()
//                .map(doc -> mongoTemplate.getConverter().read(Video.class, doc))
//                .collect(Collectors.toList());
//    }
//}

//    Aggregation aggregation = Aggregation.newAggregation(
//            Aggregation.match(Criteria.where("status").is("PUBLIC")),
//            Aggregation.project("title", "views", "likes", "publishedAt", "videoUrl", "tags")
//                    .andExpression(
//                            "((views * 0.5) + (likes * 0.3) + " +
//                                    "((?0 - (?1 - publishedAt)) * 0.0000002))", // formula as string
//                            THIRTY_DAYS_MS, now // ?0 and ?1 will be replaced with these values
//                    ).as("trendingScore"),
//            Aggregation.sort(Sort.by(Sort.Direction.DESC, "trendingScore")),
//            Aggregation.limit(limit)
//    );
//
//
//    AggregationResults<Document> results = mongoTemplate.aggregate(
//            aggregation, "videos", Document.class
//    );
//
//        return results.getMappedResults().stream()
//                .map(doc -> mongoTemplate.getConverter().read(Video.class, doc))
//            .collect(Collectors.toList());
}