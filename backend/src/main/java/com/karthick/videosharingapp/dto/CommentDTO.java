package com.karthick.videosharingapp.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private String id;

    @NotBlank(message = "Comment should not be empty")
    private String text;
    @NotBlank( message = "User id required")
    private String userId;
    @NotBlank(message = "Video id required")
    private String videoId;
    private String username;
    private String picture;
    private AtomicLong likes = new AtomicLong(0);
    private AtomicLong disLikes = new AtomicLong(0);
    private Long commentCreatedTime;

}
