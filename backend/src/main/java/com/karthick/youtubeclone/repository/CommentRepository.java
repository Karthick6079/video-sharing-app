package com.karthick.youtubeclone.repository;

import com.karthick.youtubeclone.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    Optional<List<Comment>> findByVideoId(String videoId);

    Page<Comment> findByVideoId(String videoId, Pageable pageable);
}
