package com.karthick.youtubeclone.repository;

import com.karthick.youtubeclone.dto.LatestVideoDTO;
import com.karthick.youtubeclone.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VideoRepository extends MongoRepository<Video, String> {

    Page<Video> findAll(Pageable pageable);

    Page<Video> findAllById(List<String> videoIds, Pageable pageable);
    Page<Video> findAllByUserId(List<String> userIds, Pageable pageable);
    @Aggregation(pipeline = {
            "{$match: { userId: { $in:?0}}}",
            "{$sort: { publishedDateAndTime: -1 }}",
            "{$group: {_id: '$userId',videos: { $push: '$$ROOT' }}}",
            "{$project: { _id:0,videos: { $slice: ['$videos', 2] } }}"
    })
    List<LatestVideoDTO> findLatestVideoFromUsers(List<String> userId, Pageable pageable);

    @Aggregation(pipeline = {
            "{$match: { userId: { $in:?0}}}",
            "{$sort: { publishedDateAndTime: -1 }}",
            "{$group: {_id: '$userId',videos: { $push: '$$ROOT' }}}",
            "{$project: _id:0, {videos: { $slice: ['$videos', 2] } }}"
    })
    List<LatestVideoDTO> findLatestVideoFromUsers(List<String> userId);


}
