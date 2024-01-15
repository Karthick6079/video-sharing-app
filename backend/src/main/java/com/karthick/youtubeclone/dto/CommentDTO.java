package com.karthick.youtubeclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private String id;
    @NonNull
    private String text;
    @NonNull
    private String userId;
    @NonNull
    private String videoId;
    private String username;
    private String picture;
    private Long likes;
    private Long disLikes;
    private Long commentCreatedTime;

}
