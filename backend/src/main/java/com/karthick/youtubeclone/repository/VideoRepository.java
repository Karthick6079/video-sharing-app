package com.karthick.youtubeclone.repository;

import com.karthick.youtubeclone.entity.Video;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VideoRepository extends MongoRepository<Video, String> {

}
