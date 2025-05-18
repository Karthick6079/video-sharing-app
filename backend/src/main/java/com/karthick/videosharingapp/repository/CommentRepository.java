package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    Optional<List<Comment>> findByVideoId(String videoId);

    Page<Comment> findByVideoId(String videoId, Pageable pageable);
    @Aggregation(pipeline = {
            "{$match:{ videoId: ?0}}",
            "{$group:{ _id:null,allComments:{$count:{}}}}",
            "{$project:{_id:0}}",
    })
    Long findCommentsCount(String videoId);

}
