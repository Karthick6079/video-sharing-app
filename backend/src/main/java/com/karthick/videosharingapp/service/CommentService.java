package com.karthick.videosharingapp.service;

import com.karthick.videosharingapp.entity.Comment;
import com.karthick.videosharingapp.entity.CommentDislike;
import com.karthick.videosharingapp.entity.CommentLike;
import com.karthick.videosharingapp.entity.User;
import com.karthick.videosharingapp.repository.CommentRepository;
import com.karthick.videosharingapp.repository.CommentDislikeRepository;
import com.karthick.videosharingapp.repository.CommentLikeRepository;
import com.karthick.videosharingapp.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.karthick.videosharingapp.domain.dto.CommentDTO;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MapperUtil mapper;

    private final CommentRepository commentRepository;

    private final UserService userService;

    private final CommentLikeRepository commentLikeRepository;

    private final CommentDislikeRepository commentDislikeRepository;

    private final int RECORDS_PER_PAGE = 10;

    public CommentDTO addComment(CommentDTO commentDto) {
        User user = userService.getCurrentUser();
        commentDto.setPicture(user.getPicture());
        commentDto.setUsername(user.getName());
        Comment comment = mapper.map(commentDto, Comment.class);
        comment.setCreatedAt(Instant.now());
        Comment savedComment = commentRepository.save(comment);
        return mapper.map(savedComment, CommentDTO.class);
    }

    public List<CommentDTO> getAllComments(String videoId, int page, int size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "likes")
                .and(Sort.by(Sort.Direction.ASC, "dislikes"))
                .and(Sort.by(Sort.Direction.DESC, "commentCreatedTime"));

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> commentsPage = commentRepository.findByVideoId(videoId, pageable);

        if(commentsPage.hasContent()){
            return commentsPage.getContent().stream().map(comment -> mapper.map(comment, CommentDTO.class)).toList();
        }
        return null;
    }

    public Long getCommentsCount(String videoId){
        return commentRepository.findCommentsCount(videoId);
    }



    public CommentDTO likeComment(String videoId, String userId,  String commentId){


        Optional<Comment> commentOp = commentRepository.findById(commentId);

        Optional<CommentLike> likeCommentOp = commentLikeRepository.findLikedComment( videoId, userId, commentId);

        Optional<CommentDislike> disLikedCommentOp = commentDislikeRepository.findDisLikedComment( videoId, userId, commentId);

        //If user already liked comments means decrement the like count
        if(likeCommentOp.isPresent()){
            commentOp.ifPresent(
                    comment -> {
                        comment.decrementLike();
                        commentRepository.save(comment);
                        commentLikeRepository.deleteById(likeCommentOp.get().getId());
                    }
            );
        } else if(disLikedCommentOp.isPresent()) { //If user already disliked comments means decrement the dislike count and inc like count
            commentOp.ifPresent(
                    comment -> {
                        comment.incrementLike();
                        comment.decrementDislike();
                        commentRepository.save(comment);
                        commentDislikeRepository.deleteById(disLikedCommentOp.get().getId());
                    }
            );
        } else {
            commentOp.ifPresent(
                    comment -> {
                        comment.incrementLike();
                        CommentLike commentLike = createLikeComment(videoId, userId, commentId);
                        commentRepository.save(comment);
                        System.out.println("Comment entity saved in MongoDB");
                        commentLikeRepository.save(commentLike);
                        System.out.println("CommentLike entity saved in MongoDB");
                    }
            );
        }

        return mapper.map(commentOp.orElse(new Comment()), CommentDTO.class);
    }


    public CommentDTO dislikeComment(String videoId, String userId,  String commentId){


        Optional<Comment> commentOp = commentRepository.findById(commentId);

        Optional<CommentLike> likeCommentOp = commentLikeRepository.findLikedComment( videoId, userId, commentId);

        Optional<CommentDislike> disLikedCommentOp = commentDislikeRepository.findDisLikedComment( videoId, userId, commentId);

        //If user already liked comments means decrement the like count
        if(disLikedCommentOp.isPresent()){
            commentOp.ifPresent(
                    comment -> {
                        comment.decrementDislike();
                        commentRepository.save(comment);
                        commentDislikeRepository.deleteById(disLikedCommentOp.get().getId());
                    }
            );
        } else if(likeCommentOp.isPresent()) { //If user already disliked comments means decrement the dislike count and inc like count
            commentOp.ifPresent(
                    comment -> {
                        comment.incrementDislike();
                        comment.decrementLike();
                        commentLikeRepository.deleteById(likeCommentOp.get().getId());
                        commentRepository.save(comment);
                    }
            );
        } else {
            commentOp.ifPresent(
                    comment -> {
                        comment.incrementDislike();
                        CommentDislike commentDislike = createDislikeComment(videoId, userId, commentId);
                        commentRepository.save(comment);
                        System.out.println("Comment entity saved in MongoDB");
                        commentDislikeRepository.save(commentDislike);
                        System.out.println("CommentDislike entity saved in MongoDB");
                    }
            );
        }

        return mapper.map(commentOp.orElse(new Comment()), CommentDTO.class);
    }

    public CommentLike createLikeComment(String videoId, String userId, String commentId){
        CommentLike commentLike = new CommentLike();
        commentLike.setVideoId(videoId);
        commentLike.setUserId(userId);
        commentLike.setCommentId(commentId);
        commentLike.setLikedAt(Instant.now());
        return commentLike;
    }

    public CommentDislike createDislikeComment(String videoId, String userId, String commentId){
        CommentDislike commentDislike = new CommentDislike();
        commentDislike.setVideoId(videoId);
        commentDislike.setUserId(userId);
        commentDislike.setCommentId(commentId);
        commentDislike.setDislikedAt(Instant.now());
        return commentDislike;
    }

    public <S, T> List<T> mapToList(List<S> source, Class<T> targetClassType){
        return source.stream()
                .map( sourceItem -> mapper.map(sourceItem, targetClassType))
                .collect(Collectors.toList());
    }





}
