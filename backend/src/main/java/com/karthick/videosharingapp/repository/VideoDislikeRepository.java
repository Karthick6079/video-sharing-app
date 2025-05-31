package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.entity.VideoDislike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoDislikeRepository extends MongoRepository<VideoDislike, String> {

     void deleteByUserIdAndVideoId(String userId, String videoId);

     Optional<VideoDislike> findByUserIdAndVideoId(String userId, String videoId);

}
