package com.karthick.youtubeclone.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long likes;
    private Long disLikes;
    private Long commentCreatedTime;

}
