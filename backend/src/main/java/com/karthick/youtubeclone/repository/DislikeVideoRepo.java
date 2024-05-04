package com.karthick.youtubeclone.repository;

import com.karthick.youtubeclone.entity.DislikeVideo;
import com.karthick.youtubeclone.entity.LikeVideo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DislikeVideoRepo extends MongoRepository<DislikeVideo, String> {

     void deleteByUserIdAndVideoId(String userId, String videoId);

     Optional<DislikeVideo> findByUserIdAndVideoId(String userId, String videoId);

}
