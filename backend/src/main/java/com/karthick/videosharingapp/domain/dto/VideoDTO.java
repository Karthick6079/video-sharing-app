package com.karthick.videosharingapp.domain.dto;

import com.karthick.videosharingapp.enums.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {

    private String id;
    private String title;
    private String description;
    private Set<String> tags;
    private VideoStatus videoStatus;
    private String videoUrl;
    private String thumbnailUrl;
    private Long likes;
    private Long dislikes;
    private Long viewCount;
    private Instant createdAt;
    private Instant publishedAt;
    private UserDTO userDTO;


}
