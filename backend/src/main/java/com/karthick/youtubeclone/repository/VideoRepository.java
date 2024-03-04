package com.karthick.youtubeclone.repository;

import com.karthick.youtubeclone.entity.Video;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VideoRepository extends MongoRepository<Video, String> {

    Page<Video> findAll(Pageable pageable);

    Page<Video> findAllByIds(List<String> videoIds, Pageable pageable);


}
