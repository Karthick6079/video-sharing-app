package com.karthick.youtubeclone.controller;

import com.karthick.youtubeclone.dto.CommentDTO;
import com.karthick.youtubeclone.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${spring.services.path}/video")
@RequiredArgsConstructor
@CrossOrigin
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/watch/{videoId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO addComment(@PathVariable String videoId,@RequestBody CommentDTO commentDto){
        return commentService.addComment(commentDto);
    }

    @GetMapping("/watch/{videoId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getAllComments(@PathVariable String videoId,
                                           @RequestParam( name = "page", defaultValue  = "0") int page,
                                           @RequestParam( value = "size", defaultValue = "5") int size){
        List<CommentDTO> comments = commentService.getAllComments(videoId, page, size);



        Map<String, Object> commentsMap = new LinkedHashMap<>();

        // Count only need for first call
        if(page == 0){
            Long commentsCount = commentService.getCommentsCount(videoId);
            commentsMap.put("commentsCount", commentsCount);
        }
        commentsMap.put("commentsList", comments);

        return commentsMap;
    }
}
