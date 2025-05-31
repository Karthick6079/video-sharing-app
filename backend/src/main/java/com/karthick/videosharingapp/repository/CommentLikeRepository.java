package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.entity.CommentLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends MongoRepository<CommentLike, String> {

    CommentLike findByCommentId(String commentId);

    @Query( value = "{$and:[{videoId:?0},{userId:?1},{commentId:?2}]}")
    Optional<CommentLike> findLikedComment(String videoId, String userId, String commentId);
}
