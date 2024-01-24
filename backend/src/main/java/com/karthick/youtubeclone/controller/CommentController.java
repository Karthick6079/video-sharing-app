package com.karthick.youtubeclone.controller;

import com.karthick.youtubeclone.dto.CommentDTO;
import com.karthick.youtubeclone.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<CommentDTO> getAllComments(@PathVariable String videoId,
                                           @RequestParam( name = "page", defaultValue  = "0") int page){
        return commentService.getAllComments(videoId, page);
    }
}
