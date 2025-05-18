package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.entity.LikeComment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeCommentRepo extends MongoRepository<LikeComment, String> {

    LikeComment findByCommentId(String commentId);

    @Query( value = "{$and:[{videoId:?0},{userId:?1},{commentId:?2}]}")
    Optional<LikeComment> findLikedComment(String videoId, String userId, String commentId);
}
