package com.karthick.youtubeclone.service;

import com.karthick.youtubeclone.entity.Comment;
import com.karthick.youtubeclone.entity.User;
import com.karthick.youtubeclone.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.karthick.youtubeclone.dto.CommentDTO;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final ModelMapper mapper;

    private final CommentRepository commentRepository;

    private final UserService userService;

    private final int RECORDS_PER_PAGE = 10;

    public CommentDTO addComment(CommentDTO commentDto) {
        User user = userService.getCurrentUser();
        commentDto.setCommentCreatedTime(Instant.now().toEpochMilli());
        commentDto.setPicture(user.getPicture());
        commentDto.setUsername(user.getName());
        commentDto.setLikes(0L);
        commentDto.setDisLikes(0L);
        Comment comment = mapper.map(commentDto, Comment.class);
        Comment savedComment = commentRepository.save(comment);
        return mapper.map(savedComment, CommentDTO.class);
    }

    public List<CommentDTO> getAllComments(String videoId, int page) {
        Pageable pageable = PageRequest.of(page, RECORDS_PER_PAGE, Sort.by(Sort.Direction.ASC, "commentCreatedTime"));
        Optional<List<Comment>> commentList = commentRepository.findByVideoId(videoId);
        return commentList.map(comments -> mapToList(comments, CommentDTO.class)).orElse(null);
    }

    public <S, T> List<T> mapToList(List<S> source, Class<T> targetClassType){
        return source.stream()
                .map( sourceItem -> mapper.map(sourceItem, targetClassType))
                .collect(Collectors.toList());
    }


}
